package ihh.simpleminimap;

import ihh.simpleminimap.api.SimpleMinimapApi;
import ihh.simpleminimap.api.storage.IMapChunk;
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
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(SimpleMinimapApi.modLoc("minimap"), new MinimapLayer());
    }

    private static void chunkLoad(ChunkEvent.Load event) {
        // Load the chunk into memory.
        if (event.getLevel() instanceof Level level) {
            SimpleMinimapApi.get().getMapManager().get(level).get(event.getChunk().getPos());
        }
    }

    private static void chunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            SimpleMinimapApi.get().getMapManager().get(level).ifPresent(event.getChunk().getPos(), IMapChunk::unload);
        }
    }
}
