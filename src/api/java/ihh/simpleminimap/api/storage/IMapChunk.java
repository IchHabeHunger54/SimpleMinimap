package ihh.simpleminimap.api.storage;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

/**
 * Holds the color data for a single chunk.
 */
public interface IMapChunk {
    /**
     * A chunk is 16 blocks in Minecraft.
     */
    int CHUNK_SIZE = 16;

    /**
     * @param x The x coordinate.
     * @param z The z coordinate.
     * @return The color at the given x/z coordinates.
     */
    int getColor(int x, int z);

    /**
     * Sets the given color at the given x/z coordinates.
     * @param x The x coordinate.
     * @param z The z coordinate.
     * @param color The color to set.
     */
    void setColor(int x, int z, int color);

    /**
     * Loads the given {@link ChunkAccess} into memory as a map representation.
     * As such, (re-)builds the internal color cache for this {@link IMapChunk}.
     * Call this only when necessary, as it is heavy on performance.
     * @param chunk The {@link ChunkAccess} to use to build the color cache.
     */
    void load(ChunkAccess chunk);

    /**
     * Called when the chunk is unloaded. Used for releasing resources.
     */
    void unload();

    /**
     * @return The {@link IMapLevel} associated with this {@link IMapChunk}.
     */
    IMapLevel level();

    /**
     * @return The {@link ChunkPos} associated with this {@link IMapChunk}.
     */
    ChunkPos pos();

    /**
     * Renders the chunk to the given {@link GuiGraphics}.
     * @param graphics The {@link GuiGraphics} to use.
     * @param deltaTracker The {@link DeltaTracker} to use.
     */
    void render(GuiGraphics graphics, DeltaTracker deltaTracker);

    /**
     * @return Whether all 256 positions within the chunk have been calculated or not.
     */
    boolean isComplete();

    /**
     * Calculates the color for the given {@link BlockState}.
     * @param state The {@link BlockState} to get the color for.
     * @param level The {@link Level} the {@link BlockState} is in.
     * @param pos   The {@link BlockPos} the {@link BlockState} is at.
     * @return The color for the given {@link BlockState}.
     */
    int getColorForBlockState(BlockState state, Level level, BlockPos pos);
}
