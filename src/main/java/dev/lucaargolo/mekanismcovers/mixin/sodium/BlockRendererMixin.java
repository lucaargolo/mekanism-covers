package dev.lucaargolo.mekanismcovers.mixin.sodium;

import dev.lucaargolo.mekanismcovers.MekanismCoversClient;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.quad.BakedQuadView;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.material.Material;
import mekanism.common.block.transmitter.BlockTransmitter;
import net.caffeinemc.mods.sodium.api.util.ColorARGB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockRenderer.class, remap = false)
public class BlockRendererMixin {

    @Inject(at = @At("HEAD"), method = "writeGeometry")
    public void putTranslucentVertexColor(BlockRenderContext ctx, ChunkModelBuilder builder, Vec3 offset, Material material, BakedQuadView quad, int[] colors, QuadLightData light, CallbackInfo ci) {
        if(quad.getColorIndex() == 1337 && MekanismCoversClient.isCoverTransparentFast() && ctx.state().getBlock() instanceof BlockTransmitter) {
            for (int i = 0; i < colors.length; i++) {
                colors[i] = ColorARGB.pack(ColorARGB.unpackRed(colors[i]), ColorARGB.unpackGreen(colors[i]), ColorARGB.unpackBlue(colors[i]), 255/3);
            }
        }
    }

}
