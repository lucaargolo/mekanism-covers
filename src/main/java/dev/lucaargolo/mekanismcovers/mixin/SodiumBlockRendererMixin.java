package dev.lucaargolo.mekanismcovers.mixin;

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

    @Inject(at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/model/color/ColorProvider;getColors(Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos$MutableBlockPos;Ljava/lang/Object;Lnet/caffeinemc/mods/sodium/client/model/quad/ModelQuadView;[I)V", shift = At.Shift.AFTER), method = "colorizeQuad")
    public void putTranslucentVertexColor(MutableQuadViewImpl quad, int colorIndex, CallbackInfo ci) {
        if(colorIndex == 1337 && MekanismCoversClient.isCoverTransparentFast() && this.state.getBlock() instanceof BlockTransmitter) {
            for (int i = 0; i < vertexColors.length; i++) {
                vertexColors[i] = ColorARGB.pack(ColorARGB.unpackRed(vertexColors[i]), ColorARGB.unpackGreen(vertexColors[i]), ColorARGB.unpackBlue(vertexColors[i]), 255/2);
            }
        }
    }

}