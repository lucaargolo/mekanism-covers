package dev.lucaargolo.mekanismcovers.mixin.embeddium;

import dev.lucaargolo.mekanismcovers.MekanismCoversClient;

import mekanism.common.block.transmitter.BlockTransmitter;
import net.minecraft.world.phys.Vec3;
import org.embeddedt.embeddium.api.render.chunk.BlockRenderContext;
import org.embeddedt.embeddium.api.util.ColorARGB;
import org.embeddedt.embeddium.impl.model.light.data.QuadLightData;
import org.embeddedt.embeddium.impl.model.quad.BakedQuadView;
import org.embeddedt.embeddium.impl.render.chunk.compile.buffers.ChunkModelBuilder;
import org.embeddedt.embeddium.impl.render.chunk.compile.pipeline.BlockRenderer;
import org.embeddedt.embeddium.impl.render.chunk.terrain.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockRenderer.class, remap = false)
public class EmbeddiumBlockRendererMixin {

    @Inject(at = @At("HEAD"), method = "writeGeometry")
    public void putTranslucentVertexColor(BlockRenderContext ctx, ChunkModelBuilder builder, Vec3 offset, Material material, BakedQuadView quad, int[] colors, QuadLightData light, CallbackInfo ci) {
        if(quad.getColorIndex() == 1337 && MekanismCoversClient.isCoverTransparentFast() && ctx.state().getBlock() instanceof BlockTransmitter) {
            for (int i = 0; i < colors.length; i++) {
                colors[i] = ColorARGB.pack(ColorARGB.unpackRed(colors[i]), ColorARGB.unpackGreen(colors[i]), ColorARGB.unpackBlue(colors[i]), 255/3);
            }
        }
    }

}
