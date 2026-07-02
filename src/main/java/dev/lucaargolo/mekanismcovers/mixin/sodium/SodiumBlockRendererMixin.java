package dev.lucaargolo.mekanismcovers.mixin.sodium;

import dev.lucaargolo.mekanismcovers.MekanismCoversClient;
import mekanism.common.block.transmitter.BlockTransmitter;
import net.caffeinemc.mods.sodium.api.util.ColorARGB;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockRenderer.class, remap = false)
public abstract class SodiumBlockRendererMixin extends AbstractBlockRenderContext  {

    @Shadow @Final private int[] vertexColors;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/model/color/ColorProvider;getColors", shift = At.Shift.AFTER), method = "colorizeQuad")
    public void putTranslucentVertexColor(MutableQuadViewImpl quad, int colorIndex, CallbackInfo ci) {
        if((quad.getColorIndex() == 1337 || quad.getColorIndex() == 1338) && MekanismCoversClient.isCoverTransparentFast() && this.state.getBlock() instanceof BlockTransmitter) {
            for (int i = 0; i < vertexColors.length; i++) {
                vertexColors[i] = ColorARGB.pack(ColorARGB.unpackRed(vertexColors[i]), ColorARGB.unpackGreen(vertexColors[i]), ColorARGB.unpackBlue(vertexColors[i]), 255/3);
            }
        }
    }

}