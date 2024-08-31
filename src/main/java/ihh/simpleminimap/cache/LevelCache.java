package ihh.simpleminimap.cache;

import com.mojang.blaze3d.platform.NativeImage;
import ihh.simpleminimap.SimpleMinimap;
import net.minecraft.Util;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

/**
 * Represents the on-disk cache for a level. Get via {@link CacheManager#cache()}.
 */
public class LevelCache {
    private final Path cacheDirectory;

    public LevelCache(Path cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    /**
     * Reads a {@link NativeImage} from disk, using the given {@link ChunkPos} to generate the file name.
     * @param pos The {@link ChunkPos} to use to generate the file name.
     * @return The {@link NativeImage} that has been read from disk, or null if the file did not exist.
     */
    @SuppressWarnings("resource")
    @Nullable
    public NativeImage read(ChunkPos pos) {
        Path path = cacheDirectory.resolve(pos.toLong() + ".png");
        if (!Files.exists(path)) return null;
        try {
            return Util.ioPool().submit(() -> {
                try {
                    return NativeImage.read(Files.newInputStream(path));
                } catch (IOException e) {
                    SimpleMinimap.LOGGER.warn(String.format("Could not read cached map chunk at chunk position %d/%d from path %s", pos.x, pos.z, path), e);
                    return null;
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            SimpleMinimap.LOGGER.warn(String.format("Could not read cached map chunk at chunk position %d/%d from path %s", pos.x, pos.z, path), e);
            return null;
        }
    }

    /**
     * Writes a {@link NativeImage} to disk, using the given {@link ChunkPos} to generate the file name.
     * @param pos The {@link ChunkPos} to use to generate the file name.
     * @param image The {@link NativeImage} to write.
     */
    @SuppressWarnings("resource")
    public void write(ChunkPos pos, NativeImage image) {
        Path path = cacheDirectory.resolve(pos.toLong() + ".png");
        Util.ioPool().submit(() -> {
            FileHelper.createIfAbsent(path);
            try {
                image.writeToFile(path);
            } catch (IOException e) {
                SimpleMinimap.LOGGER.warn(String.format("Could not write cached map chunk at chunk position %d/%d to path %s", pos.x, pos.z, path), e);
            }
        });
    }
}
