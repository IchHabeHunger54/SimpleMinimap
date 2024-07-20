package ihh.simpleminimap.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ihh.simpleminimap.api.SimpleMinimapApi;
import ihh.simpleminimap.api.storage.IMapChunk;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;

public class MapChunkRenderer {
    private final IMapChunk chunk;
    private DynamicTexture texture;
    private RenderType renderType;
    private boolean dirty;

    public MapChunkRenderer(IMapChunk chunk) {
        this.chunk = chunk;
    }

    public void registerTexture() {
        texture = new DynamicTexture(IMapChunk.CHUNK_SIZE, IMapChunk.CHUNK_SIZE, true);
        ResourceLocation location = SimpleMinimapApi.modLoc(chunk.level().level().dimension().location().toString().replace(':', '_') + "_" + chunk.pos().toLong());
        Minecraft.getInstance().getTextureManager().register(location, texture);
        renderType = RenderType.text(location);
    }

    public void setDirty() {
        dirty = true;
    }

    public void renderChunk(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (texture == null) {
            registerTexture();
        }
        if (dirty) {
            updateTexture();
            dirty = false;
        }
        PoseStack stack = graphics.pose();
        Matrix4f matrix4f = stack.last().pose();
        VertexConsumer vc = graphics.bufferSource().getBuffer(renderType);
        vc.addVertex(matrix4f, 0f, 128f, 0f).setColor(-1).setUv(0f, 1f).setLight(LightTexture.FULL_BRIGHT);
        vc.addVertex(matrix4f, 128f, 128f, 0f).setColor(-1).setUv(1f, 1f).setLight(LightTexture.FULL_BRIGHT);
        vc.addVertex(matrix4f, 128f, 0f, 0f).setColor(-1).setUv(1f, 0f).setLight(LightTexture.FULL_BRIGHT);
        vc.addVertex(matrix4f, 0f, 0f, 0f).setColor(-1).setUv(0f, 0f).setLight(LightTexture.FULL_BRIGHT);
    }

    private void updateTexture() {
        for (int x = 0; x < IMapChunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < IMapChunk.CHUNK_SIZE; z++) {
                texture.getPixels().setPixelRGBA(x, z, FastColor.ABGR32.opaque(chunk.getColor(x, z)));
            }
        }
        texture.upload();
    }
}
