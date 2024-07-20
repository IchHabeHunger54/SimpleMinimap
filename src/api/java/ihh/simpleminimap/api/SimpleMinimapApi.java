package ihh.simpleminimap.api;

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
public interface SimpleMinimapApi {
    String MOD_ID = "simpleminimap";

    static SimpleMinimapApi get() {
        return InstanceHolder.INSTANCE.get();
    }

    /**
     * The internal class used to hold the instances. DO NOT ACCESS YOURSELF!
     */
    @ApiStatus.Internal
    final class InstanceHolder {
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
