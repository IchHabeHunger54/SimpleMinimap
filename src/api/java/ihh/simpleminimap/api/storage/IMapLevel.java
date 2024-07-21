package ihh.simpleminimap.api.storage;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

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
     * Renders the level to the given {@link GuiGraphics}.
     * @param graphics The {@link GuiGraphics} to use.
     * @param deltaTracker The {@link DeltaTracker} to use.
     */
    void render(GuiGraphics graphics, DeltaTracker deltaTracker);
}
