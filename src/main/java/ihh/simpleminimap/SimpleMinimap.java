package ihh.simpleminimap;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = SimpleMinimap.MOD_ID, dist = Dist.CLIENT)
public class SimpleMinimap {
    public static final String MOD_ID = "simpleminimap";

    public SimpleMinimap(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(Datagen::gatherData);
        EventHandler.init(modEventBus);
    }
}
