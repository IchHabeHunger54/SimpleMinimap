package ihh.simpleminimap.cache;

import ihh.simpleminimap.api.SimpleMinimapApi;
import ihh.simpleminimap.storage.MapLevel;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Files;
import java.nio.file.Path;

public class CacheManager {
    private static final Path FOLDER_PATH = FMLPaths.getOrCreateGameRelativePath(Path.of(SimpleMinimapApi.MOD_ID));
    private static final Path INFO_FILE_PATH = FOLDER_PATH.resolve("info.txt");
    private static final String INFO_FILE_TEXT = "This folder contains cached images and metadata for the Simple Minimap mod. Each folder corresponds to one world.\n\nPlease do not edit any of the folder contents by yourself. You may delete a world's folder to clear its cache when the game is closed.";
    private static LevelCache currentLevel;

    @ApiStatus.Internal
    public static void init() {
        if (!Files.exists(INFO_FILE_PATH)) {
            FileHelper.write(INFO_FILE_PATH, INFO_FILE_TEXT);
        }
    }

    public static void setSingleplayerLevel(String folderName, Level level) {
        setLevel("singleplayer", folderName, level);
    }

    public static void setMultiplayerLevel(String folderName, Level level) {
        setLevel("multiplayer", folderName, level);
    }

    private static void setLevel(String sideFolderName, String folderName, Level level) {
        currentLevel = new LevelCache(FOLDER_PATH.resolve(sideFolderName).resolve(folderName).resolve(MapLevel.levelToId(level).replace(':', '_')));
    }

    public static LevelCache cache() {
        return currentLevel;
    }
}
