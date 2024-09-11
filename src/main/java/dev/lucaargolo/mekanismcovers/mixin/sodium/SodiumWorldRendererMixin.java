package dev.lucaargolo.mekanismcovers.mixin.sodium;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lucaargolo.mekanismcovers.MekanismCoversClient;
import dev.lucaargolo.mekanismcovers.sodium.CustomTerrainRenderPasses;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SodiumWorldRenderer.class, remap = false)
public abstract class SodiumWorldRendererMixin {

    @Shadow private RenderSectionManager renderSectionManager;

    @Inject(at = @At("HEAD"), method = "drawChunkLayer(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack;DDD)V")
    public void renderCover(RenderType renderLayer, PoseStack matrixStack, double x, double y, double z, CallbackInfo ci) {
        if(renderLayer == RenderType.translucent()) {
            ChunkRenderMatrices matrices = ChunkRenderMatrices.from(matrixStack);
            this.renderSectionManager.renderLayer(matrices, CustomTerrainRenderPasses.COVER, x, y, z);
        }
    }


}
