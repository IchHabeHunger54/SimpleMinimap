package ihh.simpleminimap.storage;

import ihh.simpleminimap.api.storage.IMapChunk;
import ihh.simpleminimap.api.storage.IMapLevel;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class MapLevel implements IMapLevel {
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
        mapChunks.putIfAbsent(pos, new MapChunk(pos, this, level.getChunk(pos.x, pos.z)));
        return mapChunks.get(pos);
    }

    @Override
    public Level level() {
        return level;
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        // TODO render all chunks that should be visible
        get(Minecraft.getInstance().player.chunkPosition()).render(graphics, deltaTracker);
    }
}
