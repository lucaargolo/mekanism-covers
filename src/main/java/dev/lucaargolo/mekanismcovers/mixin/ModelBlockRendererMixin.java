package dev.lucaargolo.mekanismcovers.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.lucaargolo.mekanismcovers.MekanismCoversClient;
import mekanism.common.block.transmitter.BlockTransmitter;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFF[IIZ)V"), method = "putQuadData", locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void putTranslucentQuadData(BlockAndTintGetter pLevel, BlockState pState, BlockPos pPos, VertexConsumer pConsumer, PoseStack.Pose pPose, BakedQuad pQuad, float pBrightness0, float pBrightness1, float pBrightness2, float pBrightness3, int pLightmap0, int pLightmap1, int pLightmap2, int pLightmap3, int pPackedOverlay, CallbackInfo ci, float f, float f1, float f2) {
        if(pQuad.getTintIndex() == 1337 && MekanismCoversClient.isCoverTransparentFast() && pState.getBlock() instanceof BlockTransmitter) {
            pConsumer.putBulkData(pPose, pQuad, new float[]{pBrightness0, pBrightness1, pBrightness2, pBrightness3}, f, f1, f2, 0.5f, new int[]{pLightmap0, pLightmap1, pLightmap2, pLightmap3}, pPackedOverlay, true);
            ci.cancel();
        }
    }

}
