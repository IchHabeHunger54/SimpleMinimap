package ihh.simpleminimap.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ihh.simpleminimap.api.storage.IMapChunk;
import ihh.simpleminimap.api.storage.IMapLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import org.joml.Matrix4f;

import java.util.Optional;

/**
 * Class holding all functionality related to rendering a {@link IMapLevel}.
 */
public class MapLevelRenderer {
    private final IMapLevel level;

    public MapLevelRenderer(IMapLevel level) {
        this.level = level;
    }

    public void renderMap(GuiGraphics graphics, float partialTick, ChunkPos fromChunk, ChunkPos toChunk) {
        int minX = fromChunk.x, maxX = toChunk.x;
        int minZ = fromChunk.z, maxZ = toChunk.z;
        PoseStack stack = graphics.pose();
        stack.pushPose();
        // Render each chunk in both directions, within the render distance, plus one chunk per direction for padding.
        for (int x = minX; x <= maxX; x++) {
            stack.pushPose();
            for (int z = minZ; z <= maxZ; z++) {
                IMapChunk chunk = level.get(new ChunkPos(x, z));
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
    }

    public void renderPlayer(GuiGraphics graphics, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        PoseStack stack = graphics.pose();
        stack.pushPose();

        // Scale up the marker itself. Constant scale multipliers and translations are copied from MapRenderer.
        float markerScale = 3f;
        stack.scale(4f * markerScale, 4f * markerScale, 1);
        stack.translate(-0.125f, 0.125f, 0f);

        // Get the player marker sprite. TODO use our own marker
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
