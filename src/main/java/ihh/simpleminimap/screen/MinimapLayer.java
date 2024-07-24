package ihh.simpleminimap.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import ihh.simpleminimap.api.SimpleMinimapApi;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

public class MinimapLayer implements LayeredDraw.Layer {
    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        // If we're in a GUI or the debug screen is active, don't show the minimap.
        if (minecraft.screen != null) return;
        if (minecraft.getDebugOverlay().showDebugScreen()) return;

        PoseStack stack = graphics.pose();
        stack.pushPose();
        SimpleMinimapApi.getMap(minecraft.level).render(graphics, deltaTracker.getGameTimeDeltaPartialTick(minecraft.isRunning()));
        stack.popPose();
    }
}
