package ihh.simpleminimap.api.storage;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

/**
 * Holds map information for a {@link Level}. Get via {@link ihh.simpleminimap.api.SimpleMinimapApi#getMap(Level)}.
 */
public interface IMapLevel {
    /**
     * Returns the {@link IMapChunk} for the given {@link ChunkPos}.
     * @param pos The {@link ChunkPos} to get the {@link IMapChunk} for.
     * @return The {@link IMapChunk} for the given {@link ChunkPos}.
     */
    IMapChunk get(ChunkPos pos);

    /**
     * Loads the chunk at the given {@link ChunkPos} into memory.
     * May delay loading the chunk into memory until it is fully available.
     * @param pos The {@link ChunkPos} of the chunk to load.
     */
    void load(ChunkPos pos);

    /**
     * Removes the chunk at the given {@link ChunkPos} from memory.
     * @param pos The {@link ChunkPos} of the chunk to remove.
     */
    void unload(ChunkPos pos);

    /**
     * @return The {@link Level} associated with this {@link IMapLevel}.
     */
    Level level();

    /**
     * Renders a part of the level, as denoted by the two given positions, using the given {@link GuiGraphics}.
     * @param graphics The {@link GuiGraphics} to use.
     * @param partialTick The partial tick amount.
     * @param fromChunk The position of the top left chunk.
     * @param toChunk The position of the bottom right chunk.
     */
    void renderMap(GuiGraphics graphics, float partialTick, ChunkPos fromChunk, ChunkPos toChunk);

    /**
     * Renders the player marker at the given {@link net.minecraft.core.BlockPos} using the given {@link GuiGraphics}.
     * Rendering will be skipped if the player isn't actually in the visible area.
     * @param graphics The {@link GuiGraphics} to use.
     * @param partialTick The partial tick amount.
     * @param fromChunk The position of the top left chunk.
     * @param toChunk The position of the bottom right chunk.
     * @param pos The position of the player.
     */
    void renderPlayer(GuiGraphics graphics, float partialTick, ChunkPos fromChunk, ChunkPos toChunk, BlockPos pos);
}
