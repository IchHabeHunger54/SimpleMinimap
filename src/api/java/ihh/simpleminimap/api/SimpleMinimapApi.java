package ihh.simpleminimap.api;

import ihh.simpleminimap.api.storage.IMapManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/**
 * The api entrypoint for Simple Minimap. Get the instance using {@link #get()}.
 */
@ApiStatus.NonExtendable
public interface SimpleMinimapApi {
    /**
     * The id of the Simple Minimap mod.
     */
    String MOD_ID = "simpleminimap";

    /**
     * @return The {@link IMapManager} instance, holding all map operations.
     */
    IMapManager getMapManager();

    /**
     * @return The only instance of this class.
     */
    static SimpleMinimapApi get() {
        return InstanceHolder.INSTANCE.get();
    }

    /**
     * Creates a {@link ResourceLocation} under the Simple Minimap mod's namespace.
     *
     * @param path The path of the {@link ResourceLocation}
     * @return A {@link ResourceLocation} with the Simple Minimap mod's namespace and the given path.
     */
    static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
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
