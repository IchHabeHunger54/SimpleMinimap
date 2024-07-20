package ihh.simpleminimap.storage;

import ihh.simpleminimap.api.storage.IMapLevel;
import ihh.simpleminimap.api.storage.IMapManager;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class MapManager implements IMapManager {
    private final Map<Level, IMapLevel> mapLevels = new HashMap<>();

    @Override
    public IMapLevel get(Level level) {
        mapLevels.putIfAbsent(level, new MapLevel(level));
        return mapLevels.get(level);
    }
}
