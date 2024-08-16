package ihh.simpleminimap;

import ihh.simpleminimap.api.SimpleMinimapApi;
import ihh.simpleminimap.cache.CacheManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = SimpleMinimapApi.MOD_ID, dist = Dist.CLIENT)
public final class SimpleMinimap {
    public static final Logger LOGGER = LogManager.getLogger();

    public SimpleMinimap(IEventBus modEventBus, ModContainer modContainer) {
        EventHandler.init(modEventBus);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.spec);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        CacheManager.init();
    }

    /**
     * Creates a {@link ResourceLocation} under the Simple Minimap mod's namespace.
     *
     * @param path The path of the {@link ResourceLocation}
     * @return A {@link ResourceLocation} with the Simple Minimap mod's namespace and the given path.
     */
    public static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(SimpleMinimapApi.MOD_ID, path);
    }
}
