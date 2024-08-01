package ihh.simpleminimap;

import com.mojang.blaze3d.platform.InputConstants;
import ihh.simpleminimap.api.SimpleMinimapApi;
import ihh.simpleminimap.screen.FullscreenMapScreen;
import ihh.simpleminimap.screen.MinimapLayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.level.ChunkEvent;
import org.lwjgl.glfw.GLFW;

final class EventHandler {
    private static final Lazy<KeyMapping> OPEN_MINIMAP_KEY = Lazy.of(() -> new KeyMapping(Translations.OPEN_MINIMAP_KEY, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, "key.categories.misc"));

    static void init(IEventBus modEventBus) {
        IEventBus neoEventBus = NeoForge.EVENT_BUS;
        modEventBus.addListener(EventHandler::registerGuiLayers);
        modEventBus.addListener(EventHandler::registerKeyMappings);
        neoEventBus.addListener(EventHandler::clientTickPost);
        neoEventBus.addListener(EventHandler::chunkLoad);
        neoEventBus.addListener(EventHandler::chunkUnload);
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(SimpleMinimap.modLoc("minimap"), new MinimapLayer());
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MINIMAP_KEY.get());
    }

    private static void clientTickPost(ClientTickEvent.Post event) {
        while (OPEN_MINIMAP_KEY.get().consumeClick()) {
            Minecraft.getInstance().setScreen(new FullscreenMapScreen());
        }
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
