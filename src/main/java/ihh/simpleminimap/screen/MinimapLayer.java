package ihh.simpleminimap.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import ihh.simpleminimap.Config;
import ihh.simpleminimap.api.SimpleMinimapApi;
import ihh.simpleminimap.api.storage.IMapChunk;
import ihh.simpleminimap.api.storage.IMapLevel;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;

public class MinimapLayer implements LayeredDraw.Layer {
    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        // If we're in a GUI or the debug screen is active, don't show the minimap.
        if (minecraft.screen != null) return;
        if (minecraft.getDebugOverlay().showDebugScreen()) return;

        int renderDistance = minecraft.options.getEffectiveRenderDistance();
        // Calculate the player's chunk position, exact position, and x/z offsets between those two.
        LocalPlayer player = minecraft.player;
        ChunkPos playerPos = player.chunkPosition();
        BlockPos exactPos = player.blockPosition();
        BlockPos offsetPos = exactPos.subtract(new Vec3i(playerPos.x * IMapChunk.CHUNK_SIZE, 0, playerPos.z * IMapChunk.CHUNK_SIZE));
        int offsetX = offsetPos.getX();
        int offsetZ = offsetPos.getZ();
        ChunkPos fromChunk = new ChunkPos(playerPos.x - renderDistance - 1, playerPos.z - renderDistance - 1);
        ChunkPos toChunk = new ChunkPos(playerPos.x + renderDistance + 1, playerPos.z + renderDistance + 1);
        PoseStack stack = graphics.pose();
        stack.pushPose();

        // Enable scissoring to the exact size of the map, so we don't draw beyond the map box.
        // (renderDistance * 16 + 8) is the exact scissor size of a map rendered at (renderDistance) scale.
        int scissorSize = renderDistance * IMapChunk.CHUNK_SIZE + IMapChunk.CHUNK_SIZE / 2;
        graphics.enableScissor(0, 0, scissorSize, scissorSize);

        // Scale the map to a size we can work with.
        float scale = 1f / Config.minimapScale.get();
        stack.scale(scale, scale, 1);

        // Translate away by the position offset.
        stack.translate(-offsetX, -offsetZ, 0);

        IMapLevel map = SimpleMinimapApi.getMap(minecraft.level);
        float gamePartialTick = deltaTracker.getGameTimeDeltaPartialTick(minecraft.isRunning());
        // Render the map background.
        map.renderMap(graphics, gamePartialTick, fromChunk, toChunk);
        // Render the player marker.
        map.renderPlayer(graphics, gamePartialTick, fromChunk, toChunk, exactPos);

        graphics.disableScissor();
        stack.popPose();
    }
}
