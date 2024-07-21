package ihh.simpleminimap.storage;

import ihh.simpleminimap.api.storage.IMapChunk;
import ihh.simpleminimap.api.storage.IMapLevel;
import ihh.simpleminimap.rendering.MapChunkRenderer;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class MapChunk implements IMapChunk {
    private final IntList colors = new IntArrayList(CHUNK_SIZE * CHUNK_SIZE);
    private final MapChunkRenderer renderer = new MapChunkRenderer(this);
    private final ChunkPos pos;
    private final IMapLevel level;

    /**
     * Constructs a new {@link MapChunk} from the given {@link ChunkAccess}.
     * @param pos The {@link ChunkPos} associated with this {@link IMapChunk}.
     * @param level The {@link IMapLevel} associated with this {@link IMapChunk}.
     * @param chunk The {@link ChunkAccess} to construct this {@link MapChunk} from.
     */
    public MapChunk(ChunkPos pos, IMapLevel level, ChunkAccess chunk) {
        this.pos = pos;
        this.level = level;
        load(chunk);
    }

    @Override
    public int getColor(int x, int z) {
        checkBounds(x, z);
        return colors.getInt(x * CHUNK_SIZE + z);
    }

    @Override
    public void setColor(int x, int z, int color) {
        checkBounds(x, z);
        setColorRaw(x, z, color);
        renderer.setDirty();
    }

    @Override
    public void load(ChunkAccess chunk) {
        // Clear color map.
        colors.clear();
        for (int i = 0; i < CHUNK_SIZE * CHUNK_SIZE; i++) {
            colors.add(-1);
        }
        // Get min and max y values from the level.
        int y = chunk.getLevel().getHeight() / 16 - 1;
        // While there are still empty spots in the chunk.
        LevelChunkSection section;
        while (!isComplete()) {
            section = chunk.getSection(y);
            // If we hit the bottom of the level, fill everything with black and break the loop.
            if (y <= 0) {
                for (int i = 0; i < CHUNK_SIZE * CHUNK_SIZE; i++) {
                    if (colors.getInt(i) == -1) {
                        colors.set(i, 0);
                    }
                }
                break;
            }
            // If there are no blocks, check next section.
            if (section.hasOnlyAir()) {
                y--;
                continue;
            }
            // Get the color for every position.
            for (int x = 0; x < CHUNK_SIZE; x++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    if (getColor(x, z) == -1) {
                        setColorRaw(x, z, getColorAtSectionXZ(chunk.getLevel(), chunk.getPos(), section, y * 16 + chunk.getLevel().getMinBuildHeight(), x, z));
                    }
                }
            }
            y--;
        }
        renderer.setDirty();
    }

    @Override
    public void unload() {
        renderer.unregisterTexture();
    }

    @Override
    public IMapLevel level() {
        return level;
    }

    @Override
    public ChunkPos pos() {
        return pos;
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        renderer.renderChunk(graphics, deltaTracker);
    }

    @Override
    public boolean isComplete() {
        return colors.intStream().allMatch(color -> color != -1);
    }

    @Override
    public int getColorForBlockState(BlockState state, Level level, BlockPos pos) {
        // TODO: use actual colors instead of map color
        return state.getMapColor(level, pos).col;
    }

    /**
     * Verifies that both parameters are within [0, CHUNK_SIZE], throwing an exception if they are not.
     * @param x The x coordinate to check.
     * @param z The z coordinate to check.
     */
    private static void checkBounds(int x, int z) {
        if (x < 0 || x >= CHUNK_SIZE || z < 0 || z >= CHUNK_SIZE)
            throw new IllegalArgumentException("Tried to access invalid map chunk position x " + x + " z " + z + ", x and z values must be between 0 and 15 (both inclusive)");
    }

    /**
     * Gets the top-most non-air block's color in the given {@link LevelChunkSection} at the given x/z coordinates.
     * @param level The {@link Level} the {@link LevelChunkSection} is in.
     * @param pos The {@link ChunkPos} of the {@link LevelChunkSection}.
     * @param section The {@link LevelChunkSection} to get the color values from.
     * @param sectionY The y coordinate of the {@link LevelChunkSection}.
     * @param x The x coordinate within the chunk.
     * @param z The z coordinate within the chunk.
     * @return The color at the given x/z coordinates, or -1 if no color was found in the given {@link LevelChunkSection} at the given x/z coordinates.
     */
    private int getColorAtSectionXZ(Level level, ChunkPos pos, LevelChunkSection section, int sectionY, int x, int z) {
        for (int localY = CHUNK_SIZE - 1; localY >= 0; localY--) {
            BlockState state = section.getBlockState(x, localY, z);
            if (state.isAir()) continue;
            int color = getColorForBlockState(state, level, new BlockPos(pos.getBlockX(x), sectionY + localY, pos.getBlockZ(z)));
            if (color > 0) return color;
        }
        return -1;
    }

    /**
     * Sets the given color at the given x/z coordinates, without updating the renderer.
     * @param x The x coordinate.
     * @param z The z coordinate.
     * @param color The color to set.
     */
    private void setColorRaw(int x, int z, int color) {
        colors.set(x * CHUNK_SIZE + z, color);
    }
}
