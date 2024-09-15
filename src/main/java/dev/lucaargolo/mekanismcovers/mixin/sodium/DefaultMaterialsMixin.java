package dev.lucaargolo.mekanismcovers.mixin.sodium;

import dev.lucaargolo.mekanismcovers.CoverRenderType;
import dev.lucaargolo.mekanismcovers.sodium.CustomTerrainRenderPasses;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.parameters.AlphaCutoffParameter;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DefaultMaterials.class, remap = false)
public class DefaultMaterialsMixin {

    @Unique
    private static final Material mekanism_covers$COVER = new Material(CustomTerrainRenderPasses.COVER, AlphaCutoffParameter.ZERO, true);

    @Inject(at = @At("HEAD"), method = "forRenderLayer", cancellable = true)
    private static void addCoverMaterial(RenderType layer, CallbackInfoReturnable<Material> cir) {
        if(layer == CoverRenderType.COVER) {
            cir.setReturnValue(mekanism_covers$COVER);
        }
    }

}
