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
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

/**
 * The in-world minimap overlay.
 */
public class MinimapLayer implements LayeredDraw.Layer {
    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        // If we're in a GUI or the debug screen is active, don't show the minimap.
        if (minecraft.screen != null) return;
        if (minecraft.getDebugOverlay().showDebugScreen()) return;

        float scale = 1f / Config.minimapScale.get();
        int renderDistance = (int) (minecraft.options.getEffectiveRenderDistance() * scale);
        // Calculate the player's chunk position, exact position, and x/z offsets between those two.
        LocalPlayer player = minecraft.player;
        ChunkPos playerPos = player.chunkPosition();
        Vec3 exactPos = player.position();
        Vec3 offsetPos = exactPos.subtract(playerPos.x * IMapChunk.CHUNK_SIZE, 0, playerPos.z * IMapChunk.CHUNK_SIZE);
        double offsetX = offsetPos.x();
        double offsetZ = offsetPos.z();
        ChunkPos fromChunk = new ChunkPos(playerPos.x - renderDistance - 1, playerPos.z - renderDistance - 1);
        ChunkPos toChunk = new ChunkPos(playerPos.x + renderDistance + 1, playerPos.z + renderDistance + 1);
        PoseStack stack = graphics.pose();
        stack.pushPose();

        // Enable scissoring to the exact size of the map, so we don't draw beyond the map box.
        // (renderDistance * 16 + 16) is the exact scissor size of a map rendered at (renderDistance) scale.
        int scissorSize = (int) ((renderDistance * IMapChunk.CHUNK_SIZE + IMapChunk.CHUNK_SIZE) * scale);
        stack.translate(-scissorSize / 2f, -scissorSize / 2f, 0);
        graphics.enableScissor(0, 0, scissorSize, scissorSize);

        stack.pushPose();

        // Scale the map to a size we can work with.
        stack.scale(scale, scale, 1);

        // Translate away by the position offset.
        stack.translate(-offsetX, -offsetZ, 0);

        IMapLevel map = SimpleMinimapApi.getMap(minecraft.level);
        float gamePartialTick = deltaTracker.getGameTimeDeltaPartialTick(minecraft.isRunning());
        // Render the map background.
        map.renderMap(graphics, gamePartialTick, fromChunk, toChunk);

        stack.popPose();
        stack.pushPose();

        // Translate away by the position offset.
        stack.translate(-offsetX * scale, -offsetZ * scale, 0);

        // Render the player marker.
        map.renderPlayer(graphics, gamePartialTick, fromChunk, toChunk, exactPos);

        stack.popPose();
        graphics.disableScissor();
        stack.popPose();
    }
}
