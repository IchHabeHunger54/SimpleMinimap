package ihh.simpleminimap.api.storage;

import ihh.simpleminimap.api.SimpleMinimapApi;
import net.minecraft.world.level.Level;

/**
 * Singleton object that holds all map information. Get via {@link SimpleMinimapApi#getMapManager()}.
 */
public interface IMapManager {
    /**
     * Returns the {@link IMapLevel} for the given {@link Level}.
     * @param level The {@link Level} to get the {@link IMapLevel} for.
     * @return The {@link IMapLevel} for the given {@link Level}.
     */
    IMapLevel get(Level level);
}
