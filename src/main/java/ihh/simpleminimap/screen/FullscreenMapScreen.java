package ihh.simpleminimap.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import ihh.simpleminimap.Config;
import ihh.simpleminimap.Translations;
import ihh.simpleminimap.api.SimpleMinimapApi;
import ihh.simpleminimap.api.storage.IMapChunk;
import ihh.simpleminimap.api.storage.IMapLevel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;

/**
 * The fullscreen map screen. Can be opened by pressing the {@link ihh.simpleminimap.EventHandler#OPEN_MINIMAP_KEY} keybind.
 */
public class FullscreenMapScreen extends Screen {
    private static Integer centerX;
    private static Integer centerY;
    private static int scale = Config.minimapScale.get();

    public FullscreenMapScreen() {
        super(Component.translatable(Translations.FULLSCREEN_MAP_TITLE));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        LocalPlayer player = minecraft.player;
        ChunkPos playerPos = player.chunkPosition();
        BlockPos exactPos = player.blockPosition();
        BlockPos offsetPos = exactPos.subtract(new Vec3i(playerPos.x * IMapChunk.CHUNK_SIZE, 0, playerPos.z * IMapChunk.CHUNK_SIZE));
        int playerOffsetX = offsetPos.getX();
        int playerOffsetZ = offsetPos.getZ();
        int guiScaledWidth = minecraft.getWindow().getGuiScaledWidth();
        int guiScaledHeight = minecraft.getWindow().getGuiScaledHeight();
        double sizeOfOneChunk = (double) IMapChunk.CHUNK_SIZE / scale;
        int widthInChunks = (int) Math.ceil(guiScaledWidth / sizeOfOneChunk);
        int heightInChunks = (int) Math.ceil(guiScaledHeight / sizeOfOneChunk);
        ChunkPos fromChunk = new ChunkPos(playerPos.x - widthInChunks / 2 - 1, playerPos.z - heightInChunks / 2 - 1);
        ChunkPos toChunk = new ChunkPos(playerPos.x + widthInChunks / 2 + 1, playerPos.z + heightInChunks / 2 + 1);
        PoseStack stack = graphics.pose();
        stack.pushPose();

        // Scale the map to a size we can work with.
        stack.scale(1f / scale, 1f / scale, 1);

        // Center the map on the player.
        stack.translate(-IMapChunk.CHUNK_SIZE - playerOffsetX, -IMapChunk.CHUNK_SIZE - playerOffsetZ, 0);
        
        IMapLevel map = SimpleMinimapApi.getMap(minecraft.level);
        // Render the map.
        map.renderMap(graphics, partialTick, fromChunk, toChunk);
        // Render the player marker.
        map.renderPlayer(graphics, partialTick, fromChunk, toChunk, exactPos);

        stack.popPose();
    }
}
