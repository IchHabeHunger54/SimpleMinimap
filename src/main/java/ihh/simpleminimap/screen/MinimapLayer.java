package ihh.simpleminimap.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import ihh.simpleminimap.api.SimpleMinimapApi;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;

public class MinimapLayer implements LayeredDraw.Layer {
    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        // If we're in a GUI or the debug screen is active, don't show the minimap.
        if (minecraft.screen != null) return;
        if (minecraft.getDebugOverlay().showDebugScreen()) return;
        // Scale to the render distance.
        PoseStack stack = graphics.pose();
        stack.pushPose();
        int width = minecraft.options.getEffectiveRenderDistance() * 2 + 1;
        float scale = 2 * minecraft.options.guiScale().get() / (float) width;
        stack.scale(scale, scale, 1);
        LocalPlayer player = minecraft.player;
        // Render the current level.
        SimpleMinimapApi.get().getMapManager().get(player.level()).render(graphics, deltaTracker);
        stack.popPose();
    }
}
