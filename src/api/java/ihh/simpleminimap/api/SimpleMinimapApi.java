package ihh.simpleminimap.api;

import ihh.simpleminimap.api.storage.IMapLevel;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/**
 * The api entrypoint for Simple Minimap.
 */
@ApiStatus.NonExtendable
public abstract class SimpleMinimapApi {
    /**
     * The id of the Simple Minimap mod.
     */
    public static final String MOD_ID = "simpleminimap";

    /**
     * Returns the {@link IMapLevel} for the given {@link Level}.
     * @param level The {@link Level} to get the {@link IMapLevel} for.
     * @return The {@link IMapLevel} for the given {@link Level}.
     */
    public static IMapLevel getMap(Level level) {
        return InstanceHolder.INSTANCE.get()._getMap(level);
    }

    @ApiStatus.Internal
    protected abstract IMapLevel _getMap(Level level);

    /**
     * The internal class used to hold the instances. DO NOT ACCESS YOURSELF!
     */
    @ApiStatus.Internal
    private static final class InstanceHolder {
        private static final Lazy<SimpleMinimapApi> INSTANCE = Lazy.of(fromServiceLoader(SimpleMinimapApi.class));

        private InstanceHolder() {}

        private static <T> Supplier<T> fromServiceLoader(Class<T> clazz) {
            return () -> {
                Optional<T> impl = ServiceLoader.load(FMLLoader.getGameLayer(), clazz).findFirst();
                String msg = "Unable to find implementation for " + clazz.getSimpleName() + "!";
                if (!FMLEnvironment.production) {
                    return impl.orElseThrow(() -> {
                        IllegalStateException exception = new IllegalStateException(msg);
                        LoggerFactory.getLogger(MOD_ID).error(exception.getMessage(), exception);
                        return exception;
                    });
                }
                return impl.orElseGet(() -> {
                    LoggerFactory.getLogger(MOD_ID).error(msg);
                    return null;
                });
            };
        }
    }
}
