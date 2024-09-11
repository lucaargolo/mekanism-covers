package dev.lucaargolo.mekanismcovers.mixin.sodium;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lucaargolo.mekanismcovers.CoverRenderType;
import dev.lucaargolo.mekanismcovers.MekanismCovers;
import dev.lucaargolo.mekanismcovers.MekanismCoversClient;
import dev.lucaargolo.mekanismcovers.sodium.CustomTerrainRenderPasses;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.registries.MekanismItems;
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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = SodiumWorldRenderer.class, remap = false)
public abstract class SodiumWorldRendererMixin {

    @Shadow @Final private Minecraft client;
    @Shadow private RenderSectionManager renderSectionManager;

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;translucent()Lnet/minecraft/client/renderer/RenderType;"), method = "drawChunkLayer", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void renderCover(RenderType renderLayer, PoseStack matrixStack, double x, double y, double z, CallbackInfo ci, ChunkRenderMatrices matrices) {
        if(renderLayer == RenderType.translucent()) {
            MekanismCoversClient.updateTransparency(client.player);
            this.renderSectionManager.renderLayer(matrices, CustomTerrainRenderPasses.COVER, x, y, z);
        }
    }


}
