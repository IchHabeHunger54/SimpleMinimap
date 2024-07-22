package ihh.simpleminimap.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import ihh.simpleminimap.api.storage.IMapChunk;
import ihh.simpleminimap.api.storage.IMapLevel;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapLevel implements IMapLevel {
    private final List<ChunkPos> chunkLoadQueue = new ArrayList<>();
    private final Map<ChunkPos, IMapChunk> mapChunks = new HashMap<>();
    private final Level level;

    /**
     * @param level The {@link Level} associated with this {@link IMapLevel}.
     */
    public MapLevel(Level level) {
        this.level = level;
    }

    @Override
    public IMapChunk get(ChunkPos pos) {
        if (!mapChunks.containsKey(pos)) load(pos);
        return mapChunks.get(pos);
    }

    @Override
    public void load(ChunkPos pos) {
        LevelChunk chunk = level.getChunk(pos.x, pos.z);
        if (chunk.getPersistedStatus() == ChunkStatus.FULL) {
            putChunk(pos, chunk);
        } else {
            chunkLoadQueue.add(pos);
        }
    }

    @Override
    public void unload(ChunkPos pos) {
        if (mapChunks.containsKey(pos)) {
            mapChunks.get(pos).unload();
        }
    }

    @Override
    public Level level() {
        return level;
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        // Construct chunks that have been fully loaded in the meantime.
        for (ChunkPos pos : chunkLoadQueue) {
            LevelChunk chunk = level.getChunk(pos.x, pos.z);
            if (chunk.getPersistedStatus() == ChunkStatus.FULL) {
                putChunk(pos, chunk);
                chunkLoadQueue.remove(pos);
            }
        }
        Minecraft minecraft = Minecraft.getInstance();
        PoseStack stack = graphics.pose();
        stack.pushPose();
        // Render each chunk in both directions, within the render distance.
        int renderDistance = minecraft.options.getEffectiveRenderDistance();
        ChunkPos playerPos = minecraft.player.chunkPosition();
        for (int x = -renderDistance; x <= renderDistance; x++) {
            stack.pushPose();
            for (int z = -renderDistance; z <= renderDistance; z++) {
                stack.pushPose();
                IMapChunk chunk = get(new ChunkPos(playerPos.x + x, playerPos.z + z));
                if (chunk != null) {
                    chunk.render(graphics, deltaTracker);
                }
                stack.popPose();
                stack.translate(0, IMapChunk.CHUNK_SIZE, 0);
            }
            stack.popPose();
            stack.translate(IMapChunk.CHUNK_SIZE, 0, 0);
        }
        stack.popPose();
    }

    /**
     * Actually puts the chunk into the map.
     * @param pos The {@link ChunkPos} of the chunk.
     * @param chunk The {@link ChunkAccess} to put into the map.
     */
    private void putChunk(ChunkPos pos, ChunkAccess chunk) {
        mapChunks.put(pos, new MapChunk(pos, this, chunk));
    }
}
