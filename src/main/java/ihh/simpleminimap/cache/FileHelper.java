package ihh.simpleminimap.cache;

import ihh.simpleminimap.SimpleMinimap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileHelper {
    public static void createIfAbsent(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                SimpleMinimap.LOGGER.warn(String.format("Could not create file at %s", path), e);
            }
        }
    }

    public static void write(Path path, String content) {
        createIfAbsent(path);
        try {
            Files.writeString(path, content);
        } catch (IOException e) {
            SimpleMinimap.LOGGER.warn(String.format("Could not write to file at %s", path), e);
        }
    }
}
