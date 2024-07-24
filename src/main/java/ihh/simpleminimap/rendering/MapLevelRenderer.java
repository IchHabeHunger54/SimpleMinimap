package ihh.simpleminimap.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import ihh.simpleminimap.api.storage.IMapChunk;
import ihh.simpleminimap.api.storage.IMapLevel;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import org.joml.Matrix4f;

import java.util.Optional;

public class MapLevelRenderer {
    private final IMapLevel level;

    public MapLevelRenderer(IMapLevel level) {
        this.level = level;
    }

    public void renderMap(GuiGraphics graphics, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        int renderDistance = minecraft.options.getEffectiveRenderDistance();
        // Calculate the player's chunk position, exact position, and x/z offsets between those two.
        LocalPlayer player = minecraft.player;
        ChunkPos playerPos = player.chunkPosition();
        BlockPos offsetPos = player.blockPosition().subtract(new Vec3i(playerPos.x * 16, 0, playerPos.z * 16));
        int offsetX = offsetPos.getX();
        int offsetZ = offsetPos.getZ();
        PoseStack stack = graphics.pose();
        stack.pushPose();

        // Enable scissoring to the exact size of the map, so we don't draw beyond the map box.
        // (renderDistance * 16 + 8) is the exact scissor size of a map rendered at (renderDistance) scale.
        int scissorSize = renderDistance * IMapChunk.CHUNK_SIZE + IMapChunk.CHUNK_SIZE / 2;
        graphics.enableScissor(0, 0, scissorSize, scissorSize);

        // Scale the map to a size we can work with.
        float scale = 1 / 2f; // TODO config
        stack.scale(scale, scale, 1);

        stack.pushPose();
        // Translate away by the position offset.
        stack.translate(-offsetX, -offsetZ, 0);

        // Render each chunk in both directions, within the render distance, plus one chunk per direction for padding.
        for (int x = -renderDistance - 1; x <= renderDistance + 1; x++) {
            stack.pushPose();
            for (int z = -renderDistance - 1; z <= renderDistance + 1; z++) {
                IMapChunk chunk = level.get(new ChunkPos(playerPos.x + x, playerPos.z + z));
                if (chunk != null) {
                    chunk.render(graphics, partialTick);
                }
                stack.translate(0, IMapChunk.CHUNK_SIZE, 0);
            }
            stack.popPose();
            stack.translate(IMapChunk.CHUNK_SIZE, 0, 0);
        }
        stack.popPose();

        // Flush the render type of the last chunk.
        graphics.flush();
        graphics.disableScissor();
        stack.popPose();
    }

    public void renderPlayerMarker(GuiGraphics graphics, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        int renderDistance = minecraft.options.getEffectiveRenderDistance();
        LocalPlayer player = minecraft.player;
        ChunkPos playerPos = player.chunkPosition();
        BlockPos offsetPos = player.blockPosition().subtract(new Vec3i(playerPos.x * 16, 0, playerPos.z * 16));
        int offsetX = offsetPos.getX();
        int offsetZ = offsetPos.getZ();
        PoseStack stack = graphics.pose();
        stack.pushPose();

        // Scale the map to a size we can work with.
        float scale = 1 / 2f; // TODO config
        stack.scale(scale, scale, 1);

        // Translate to the center of the map.
        stack.translate((renderDistance + 1) * IMapChunk.CHUNK_SIZE, (renderDistance + 1) * IMapChunk.CHUNK_SIZE, 0);
        //stack.translate(renderDistance * IMapChunk.CHUNK_SIZE, renderDistance * IMapChunk.CHUNK_SIZE, 0);

        // Rotate for the player's view.
        stack.mulPose(Axis.ZP.rotationDegrees(player.getYHeadRot()));

        // Scale up the marker itself. Constant scale multipliers and translations are copied from MapRenderer.
        float markerScale = 3f;
        stack.scale(4f * markerScale, 4f * markerScale, 1);
        stack.translate(-0.125f, 0.125f, 0f);

        // Get the player marker sprite.
        TextureAtlasSprite sprite = minecraft.getMapDecorationTextures().get(new MapDecoration(MapDecorationTypes.PLAYER, (byte) 0, (byte) 0, (byte) 0, Optional.empty()));

        // Render the sprite.
        Matrix4f matrix4f = stack.last().pose();
        float z = -0.001f;
        float startU = sprite.getU0();
        float startV = sprite.getV0();
        float endU = sprite.getU1();
        float endV = sprite.getV1();
        VertexConsumer vc = graphics.bufferSource().getBuffer(RenderType.text(sprite.atlasLocation()));
        vc.addVertex(matrix4f, -1f, 1f, z).setColor(-1).setUv(startU, startV).setLight(LightTexture.FULL_BRIGHT);
        vc.addVertex(matrix4f, 1f, 1f, z).setColor(-1).setUv(endU, startV).setLight(LightTexture.FULL_BRIGHT);
        vc.addVertex(matrix4f, 1f, -1f, z).setColor(-1).setUv(endU, endV).setLight(LightTexture.FULL_BRIGHT);
        vc.addVertex(matrix4f, -1f, -1f, z).setColor(-1).setUv(startU, endV).setLight(LightTexture.FULL_BRIGHT);

        stack.popPose();
    }
}
