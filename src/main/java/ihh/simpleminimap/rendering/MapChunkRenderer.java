package ihh.simpleminimap.rendering;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ihh.simpleminimap.SimpleMinimap;
import ihh.simpleminimap.api.storage.IMapChunk;
import ihh.simpleminimap.cache.CacheManager;
import ihh.simpleminimap.storage.MapLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;

/**
 * Class holding all functionality related to rendering a single {@link IMapChunk}.
 */
public class MapChunkRenderer {
    private final IMapChunk chunk;
    private DynamicTexture texture;
    private ResourceLocation textureId;
    private RenderType renderType;
    private NativeImage cachedImage;
    private boolean dirty;

    public MapChunkRenderer(IMapChunk chunk) {
        this.chunk = chunk;
    }

    /**
     * Creates the dynamic texture and registers it to the texture manager.
     */
    public void registerTexture() {
        texture = new DynamicTexture(IMapChunk.CHUNK_SIZE, IMapChunk.CHUNK_SIZE, true);
        textureId = SimpleMinimap.modLoc(MapLevel.levelToId(chunk.level().level()).replace(':', '_') + "_" + chunk.pos().toLong());
        Minecraft.getInstance().getTextureManager().register(textureId, texture);
        renderType = RenderType.text(textureId);
    }
    /**
     * Mark the renderer as dirty, causing the texture to be refreshed before the next render call.
     */
    public void setDirty() {
        dirty = true;
    }

    /**
     * Use the given {@link NativeImage} for rendering.
     * @param image The {@link NativeImage} to use for rendering.
     */
    public void useNativeImage(NativeImage image) {
        this.cachedImage = image;
        dirty = true;
    }

    /**
     * Renders the chunk map to the given {@link GuiGraphics}.
     * @param graphics The {@link GuiGraphics} to use.
     * @param partialTick The partial tick amount.
     */
    public void renderChunk(GuiGraphics graphics, float partialTick) {
        if (texture == null) {
            registerTexture();
            dirty = true;
        }
        if (dirty) {
            updateTexture();
            dirty = false;
        }
        PoseStack stack = graphics.pose();
        Matrix4f matrix4f = stack.last().pose();
        VertexConsumer vc = graphics.bufferSource().getBuffer(renderType);
        vc.addVertex(matrix4f, 0f, 16f, 0f).setColor(-1).setUv(0f, 1f).setLight(LightTexture.FULL_BRIGHT);
        vc.addVertex(matrix4f, 16f, 16f, 0f).setColor(-1).setUv(1f, 1f).setLight(LightTexture.FULL_BRIGHT);
        vc.addVertex(matrix4f, 16f, 0f, 0f).setColor(-1).setUv(1f, 0f).setLight(LightTexture.FULL_BRIGHT);
        vc.addVertex(matrix4f, 0f, 0f, 0f).setColor(-1).setUv(0f, 0f).setLight(LightTexture.FULL_BRIGHT);
    }

    /**
     * Removes the texture from the texture manager.
     */
    public void unregisterTexture() {
        if (textureId != null) {
            Minecraft.getInstance().getTextureManager().release(textureId);
        }
        texture = null;
        textureId = null;
        renderType = null;
    }

    /**
     * Sets the pixels to the texture and sends the texture to memory.
     */
    private void updateTexture() {
        if (cachedImage != null) {
            texture.setPixels(cachedImage);
            cachedImage = null;
        } else {
            for (int x = 0; x < IMapChunk.CHUNK_SIZE; x++) {
                for (int z = 0; z < IMapChunk.CHUNK_SIZE; z++) {
                    int color = chunk.getColor(x, z);
                    texture.getPixels().setPixelRGBA(x, z, FastColor.ABGR32.fromArgb32(0xff000000 | color));
                }
            }
        }
        texture.upload();
        if (!chunk.isEmpty()) {
            CacheManager.cache().write(chunk.pos(), texture.getPixels());
        }
    }
}
