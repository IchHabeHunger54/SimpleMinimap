package ihh.simpleminimap.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import ihh.simpleminimap.Translations;
import ihh.simpleminimap.api.SimpleMinimapApi;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;

public class FullscreenMapScreen extends Screen {
    public FullscreenMapScreen() {
        super(Component.translatable(Translations.FULLSCREEN_MAP_TITLE));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        ChunkPos playerPos = getMinecraft().player.chunkPosition();
        int renderDistance = getMinecraft().options.getEffectiveRenderDistance();
        ChunkPos fromChunk = new ChunkPos(playerPos.x - renderDistance - 1, playerPos.z - renderDistance - 1);
        ChunkPos toChunk = new ChunkPos(playerPos.x + renderDistance + 1, playerPos.z + renderDistance + 1);

        PoseStack stack = graphics.pose();
        stack.pushPose();
        SimpleMinimapApi.getMap(getMinecraft().level).renderMap(graphics, partialTick, fromChunk, toChunk);
        stack.popPose();
    }
}
