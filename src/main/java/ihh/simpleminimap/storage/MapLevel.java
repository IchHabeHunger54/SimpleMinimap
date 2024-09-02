package ihh.simpleminimap.storage;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import ihh.simpleminimap.api.storage.IMapChunk;
import ihh.simpleminimap.api.storage.IMapLevel;
import ihh.simpleminimap.cache.CacheManager;
import ihh.simpleminimap.rendering.MapLevelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapLevel implements IMapLevel {
    private final List<ChunkPos> chunkLoadQueue = new ArrayList<>();
    private final List<ChunkPos> chunkConstructQueue = new ArrayList<>();
    private final Map<ChunkPos, IMapChunk> mapChunks = new HashMap<>();
    private final MapLevelRenderer renderer = new MapLevelRenderer(this);
    private final Level level;

    /**
     * @param level The {@link Level} associated with this {@link IMapLevel}.
     */
    public MapLevel(Level level) {
        this.level = level;
        CacheManager.cache().readAllAsync().thenAcceptAsync(map -> map.forEach((pos, image) -> {
            if (!mapChunks.containsKey(pos) || mapChunks.get(pos).isEmpty()) {
                putChunk(pos, image);
            }
        }));
    }

    /**
     * @param level The {@link Level} to convert to a string.
     * @return The string id of the given {@link Level}.
     */
    public static String levelToId(Level level) {
        return level.dimension().location().toString();
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
    public void renderMap(GuiGraphics graphics, float partialTick, ChunkPos fromChunk, ChunkPos toChunk) {
        // Load available chunks from disk.
        for (ChunkPos pos : chunkLoadQueue) {
            NativeImage image = CacheManager.cache().read(pos);
            if (image != null) {
                putChunk(pos, image);
            } else {
                chunkConstructQueue.add(pos);
            }
        }
        chunkLoadQueue.clear();
        // Construct chunks that have been fully loaded in the meantime.
        for (ChunkPos pos : chunkConstructQueue) {
            LevelChunk chunk = level.getChunk(pos.x, pos.z);
            if (chunk.getPersistedStatus() == ChunkStatus.FULL) {
                putChunk(pos, chunk);
                chunkConstructQueue.remove(pos);
            }
        }

        renderer.renderMap(graphics, partialTick, fromChunk, toChunk);
    }

    @Override
    public void renderPlayer(GuiGraphics graphics, float partialTick, ChunkPos fromChunk, ChunkPos toChunk, Vec3 pos) {
        // Return if the pos is outside the rendered chunk range.
        if (pos.x() < fromChunk.x * IMapChunk.CHUNK_SIZE || pos.x() > toChunk.x * IMapChunk.CHUNK_SIZE || pos.z() < fromChunk.z * IMapChunk.CHUNK_SIZE || pos.z() > toChunk.z * IMapChunk.CHUNK_SIZE)
            return;

        PoseStack stack = graphics.pose();
        stack.pushPose();

        // Translate to the player position.
        stack.translate(pos.x() - fromChunk.x * IMapChunk.CHUNK_SIZE, pos.z() - fromChunk.z * IMapChunk.CHUNK_SIZE, 0);
        // Rotate to the player's rotation.
        stack.mulPose(Axis.ZP.rotationDegrees(Minecraft.getInstance().player.getViewYRot(partialTick)));

        renderer.renderPlayer(graphics, partialTick);

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

    /**
     * Actually puts the chunk into the map.
     * @param pos The {@link ChunkPos} of the chunk.
     * @param image The {@link NativeImage} to put into the map.
     */
    private void putChunk(ChunkPos pos, NativeImage image) {
        mapChunks.put(pos, new MapChunk(pos, this, image));
    }
}
