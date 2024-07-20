package ihh.simpleminimap.data;

import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.function.Function;

@EventBusSubscriber
class SimpleMinimapDatagen {
    @SubscribeEvent
    static void gatherData(GatherDataEvent event) {
        clientProvider(event, Language::new);
    }

    private static void clientProvider(GatherDataEvent event, Function<PackOutput, DataProvider> provider) {
        event.getGenerator().<DataProvider>addProvider(event.includeClient(), provider::apply);
    }
}
