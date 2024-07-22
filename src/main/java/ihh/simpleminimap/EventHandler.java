package ihh.simpleminimap;

import ihh.simpleminimap.api.SimpleMinimapApi;
import ihh.simpleminimap.screen.MinimapLayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;

final class EventHandler {
    static void init(IEventBus modEventBus) {
        IEventBus neoEventBus = NeoForge.EVENT_BUS;
        modEventBus.addListener(EventHandler::registerGuiLayers);
        neoEventBus.addListener(EventHandler::chunkLoad);
        neoEventBus.addListener(EventHandler::chunkUnload);
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(SimpleMinimap.modLoc("minimap"), new MinimapLayer());
    }

    private static void chunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof Level level) {
            SimpleMinimapApi.getMap(level).load(event.getChunk().getPos());
        }
    }

    private static void chunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            SimpleMinimapApi.getMap(level).unload(event.getChunk().getPos());
        }
    }
}
