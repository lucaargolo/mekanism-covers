package dev.lucaargolo.mekanismcovers.mixin;

import dev.lucaargolo.mekanismcovers.CoverRenderType;
import dev.lucaargolo.mekanismcovers.MekanismCoversClient;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {


    @Shadow protected abstract void renderSectionLayer(RenderType renderType, double x, double y, double z, Matrix4f frustrumMatrix, Matrix4f projectionMatrix);

    @Unique
    boolean mekanism_covers$isRenderingCover = false;

    @Inject(at = @At("HEAD"), method = "renderSectionLayer")
    public void renderCover(RenderType renderType, double x, double y, double z, Matrix4f frustrumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        if(renderType == RenderType.translucent()) {
            mekanism_covers$isRenderingCover = true;
            MekanismCoversClient.updateShaderTransparency();
            renderSectionLayer(CoverRenderType.COVER, x, y, z, frustrumMatrix, projectionMatrix);
            mekanism_covers$isRenderingCover = false;
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;translucent()Lnet/minecraft/client/renderer/RenderType;"), method = "renderSectionLayer")
    public RenderType redirectTranslucentCoverLayer(RenderType original) {
        if(mekanism_covers$isRenderingCover) {
            return CoverRenderType.COVER;
        }else {
            return original;
        }
    }


}
