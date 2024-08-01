package ihh.simpleminimap.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import ihh.simpleminimap.Config;
import ihh.simpleminimap.Translations;
import ihh.simpleminimap.api.SimpleMinimapApi;
import ihh.simpleminimap.api.storage.IMapChunk;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;

public class FullscreenMapScreen extends Screen {
    public FullscreenMapScreen() {
        super(Component.translatable(Translations.FULLSCREEN_MAP_TITLE));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        int renderDistance = getMinecraft().options.getEffectiveRenderDistance();
        LocalPlayer player = getMinecraft().player;
        ChunkPos playerPos = player.chunkPosition();
        BlockPos exactPos = player.blockPosition();
        BlockPos offsetPos = exactPos.subtract(new Vec3i(playerPos.x * IMapChunk.CHUNK_SIZE, 0, playerPos.z * IMapChunk.CHUNK_SIZE));
        int offsetX = offsetPos.getX();
        int offsetZ = offsetPos.getZ();
        ChunkPos fromChunk = new ChunkPos(playerPos.x - renderDistance - 1, playerPos.z - renderDistance - 1);
        ChunkPos toChunk = new ChunkPos(playerPos.x + renderDistance + 1, playerPos.z + renderDistance + 1);
        PoseStack stack = graphics.pose();
        stack.pushPose();

        // Move the map to the center of the screen.
        stack.translate(minecraft.getWindow().getGuiScaledWidth() / 2f, minecraft.getWindow().getGuiScaledHeight() / 2f, 0);

        // Scale the map to a size we can work with.
        float scale = 1f / Config.minimapScale.get();
        stack.scale(scale, scale, 1);

        // Center the map on the player.
        stack.translate(-renderDistance * IMapChunk.CHUNK_SIZE - IMapChunk.CHUNK_SIZE / 2f, -renderDistance * IMapChunk.CHUNK_SIZE - IMapChunk.CHUNK_SIZE / 2f, 0);
        stack.translate(-offsetX, -offsetZ, 0);

        SimpleMinimapApi.getMap(getMinecraft().level).renderMap(graphics, partialTick, fromChunk, toChunk);

        stack.popPose();
    }
}
