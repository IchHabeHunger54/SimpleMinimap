package ihh.simpleminimap.cache;

import ihh.simpleminimap.SimpleMinimap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Holds various helper methods for file handling.
 */
public final class FileHelper {
    /**
     * Creates an empty file at the given path if it doesn't exist yet.
     * @param path The path to create the file at.
     */
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

    /**
     * Writes the given string into the file at the given path.
     * @param path The path of the file to write to.
     * @param content The string to write.
     */
    public static void write(Path path, String content) {
        createIfAbsent(path);
        try {
            Files.writeString(path, content);
        } catch (IOException e) {
            SimpleMinimap.LOGGER.warn(String.format("Could not write to file at %s", path), e);
        }
    }
}
