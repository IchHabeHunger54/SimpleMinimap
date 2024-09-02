package ihh.simpleminimap.cache;

import com.mojang.blaze3d.platform.NativeImage;
import ihh.simpleminimap.SimpleMinimap;
import net.minecraft.Util;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

/**
 * Represents the on-disk cache for a level. Get via {@link CacheManager#cache()}.
 */
@SuppressWarnings("resource")
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
    @Nullable
    public NativeImage read(ChunkPos pos) {
        Path path = cacheDirectory.resolve(pos.toLong() + ".png");
        if (!Files.exists(path)) return null;
        try {
            return Util.backgroundExecutor().submit(() -> {
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
    public void write(ChunkPos pos, NativeImage image) {
        Path path = cacheDirectory.resolve(pos.toLong() + ".png");
        Util.backgroundExecutor().submit(() -> {
            FileHelper.createIfAbsent(path);
            try {
                image.writeToFile(path);
            } catch (IOException e) {
                SimpleMinimap.LOGGER.warn(String.format("Could not write cached map chunk at chunk position %d/%d to path %s", pos.x, pos.z, path), e);
            }
        });
    }

    /**
     * Asynchronously reads all files in the cache directory and returns them as a map of {@link ChunkPos} to {@link NativeImage}.
     * @return A {@link CompletableFuture} holding a map of {@link ChunkPos} to {@link NativeImage}, representing all cached map chunk.
     */
    public CompletableFuture<Map<ChunkPos, NativeImage>> readAllAsync() {
        if (!Files.exists(cacheDirectory)) return CompletableFuture.completedFuture(Map.of());
        return CompletableFuture.supplyAsync(() -> {
            Map<ChunkPos, NativeImage> map = new HashMap<>();
            try (Stream<Path> stream = Files.walk(cacheDirectory)) {
                stream.filter(path -> path.getFileName().toString().endsWith(".png")).forEach(path -> {
                    try {
                        ChunkPos pos = new ChunkPos(Long.parseLong(path.getFileName().toString().replace(".png", "")));
                        InputStream inputStream = Files.newInputStream(path);
                        map.put(pos, NativeImage.read(inputStream));
                        inputStream.close();
                    } catch (NumberFormatException | IOException e) {
                        SimpleMinimap.LOGGER.warn(String.format("Could not read cached map chunk from path %s", path), e);
                    }
                });
            } catch (IOException e) {
                SimpleMinimap.LOGGER.warn(String.format("Failed to load all chunks for the current level from path %s", cacheDirectory), e);
            }
            return map;
        });
    }
}
