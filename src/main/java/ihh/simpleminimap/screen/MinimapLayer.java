package ihh.simpleminimap.screen;

import ihh.simpleminimap.api.SimpleMinimapApi;
import ihh.simpleminimap.api.storage.IMapLevel;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;

public class MinimapLayer implements LayeredDraw.Layer {
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        // If we're in a GUI or the debug screen is active, don't show the minimap.
        if (minecraft.screen != null) return;
        if (minecraft.getDebugOverlay().showDebugScreen()) return;
        LocalPlayer player = minecraft.player;
        // Render the current chunk.
        IMapLevel mapLevel = SimpleMinimapApi.get().getMapManager().get(player.level());
        mapLevel.get(player.chunkPosition()).render(guiGraphics, deltaTracker);
    }
}
