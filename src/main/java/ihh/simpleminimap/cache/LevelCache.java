package ihh.simpleminimap.cache;

import com.mojang.blaze3d.platform.NativeImage;
import ihh.simpleminimap.SimpleMinimap;
import net.minecraft.Util;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

public class LevelCache {
    private final Path cacheDirectory;

    public LevelCache(Path cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    @Nullable
    public NativeImage read(ChunkPos pos) {
        return null; //TODO
    }

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
