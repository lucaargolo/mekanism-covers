package dev.lucaargolo.mekanismcovers.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lucaargolo.mekanismcovers.CoverRecipe;
import dev.lucaargolo.mekanismcovers.CoverRenderType;
import dev.lucaargolo.mekanismcovers.MekanismCovers;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tags.MekanismTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow protected abstract void renderChunkLayer(RenderType pRenderType, PoseStack pPoseStack, double pCamX, double pCamY, double pCamZ, Matrix4f pProjectionMatrix);

    @Shadow @Final private Minecraft minecraft;
    @Unique
    boolean mekanism_covers$isRenderingCover = false;

    @Inject(at = @At("HEAD"), method = "renderChunkLayer")
    public void renderCover(RenderType pRenderType, PoseStack pPoseStack, double pCamX, double pCamY, double pCamZ, Matrix4f pProjectionMatrix, CallbackInfo ci) {
        if(pRenderType == RenderType.translucent()) {
            mekanism_covers$isRenderingCover = true;
            if(minecraft.player != null) {
                ItemStack stack = minecraft.player.getMainHandItem();
                if(stack.is(MekanismItems.CONFIGURATOR.get())) {
                    float f = MekanismItems.CONFIGURATOR.get().getMode(stack) == ItemConfigurator.ConfiguratorMode.valueOf("COVER") ? 1.0f : 0.25f;
                    MekanismCovers.Client.COVER_TRANSPARENCY.set(f);
                }else{
                    MekanismCovers.Client.COVER_TRANSPARENCY.set(1.0f);
                }
            }else{
                MekanismCovers.Client.COVER_TRANSPARENCY.set(1.0f);
            }
            renderChunkLayer(CoverRenderType.COVER, pPoseStack, pCamX, pCamY, pCamZ, pProjectionMatrix);
            mekanism_covers$isRenderingCover = false;
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;translucent()Lnet/minecraft/client/renderer/RenderType;"), method = "renderChunkLayer")
    public RenderType redirectTranslucentCoverLayer(RenderType original) {
        if(mekanism_covers$isRenderingCover) {
            return CoverRenderType.COVER;
        }else {
            return original;
        }
    }


}
