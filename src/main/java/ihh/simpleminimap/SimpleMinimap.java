package ihh.simpleminimap;

import ihh.simpleminimap.api.SimpleMinimapApi;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = SimpleMinimapApi.MOD_ID, dist = Dist.CLIENT)
public final class SimpleMinimap {
    public SimpleMinimap(IEventBus modEventBus, ModContainer modContainer) {
        EventHandler.init(modEventBus);
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
