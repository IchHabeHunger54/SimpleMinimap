package ihh.simpleminimap.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import ihh.simpleminimap.Translations;
import ihh.simpleminimap.api.SimpleMinimapApi;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FullscreenMapScreen extends Screen {
    public FullscreenMapScreen() {
        super(Component.translatable(Translations.FULLSCREEN_MAP_TITLE));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        PoseStack stack = graphics.pose();
        stack.pushPose();
        SimpleMinimapApi.getMap(getMinecraft().level).render(graphics, partialTick);
        stack.popPose();
    }
}
