package ihh.simpleminimap;

import ihh.simpleminimap.api.SimpleMinimapApi;
import ihh.simpleminimap.api.storage.IMapLevel;
import ihh.simpleminimap.storage.MapLevel;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public final class SimpleMinimapApiImpl extends SimpleMinimapApi {
    private final Map<Level, IMapLevel> mapLevels = new HashMap<>();

    @Override
    protected IMapLevel _getMap(Level level) {
        mapLevels.putIfAbsent(level, new MapLevel(level));
        return mapLevels.get(level);
    }
}
