package ihh.simpleminimap.api.storage;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

/**
 * Holds map information for a {@link Level}. Get via {@link IMapManager#get(Level)}.
 */
public interface IMapLevel {
    /**
     * Returns the {@link IMapChunk} for the given {@link ChunkPos}.
     * @param pos The {@link ChunkPos} to get the {@link IMapChunk} for.
     * @return The {@link IMapChunk} for the given {@link ChunkPos}.
     */
    IMapChunk get(ChunkPos pos);

    /**
     * @return The {@link Level} associated with this {@link IMapLevel}.
     */
    Level level();

    /**
     * Renders the level to the given {@link GuiGraphics}.
     * @param graphics The {@link GuiGraphics} to use.
     * @param deltaTracker The {@link DeltaTracker} to use.
     */
    void render(GuiGraphics graphics, DeltaTracker deltaTracker);
}
