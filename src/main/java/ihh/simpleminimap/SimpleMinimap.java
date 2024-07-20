package ihh.simpleminimap;

import ihh.simpleminimap.api.SimpleMinimapApi;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = SimpleMinimapApi.MOD_ID, dist = Dist.CLIENT)
public class SimpleMinimap {
    public SimpleMinimap(IEventBus modEventBus, ModContainer modContainer) {
        EventHandler.init(modEventBus);
    }
}
