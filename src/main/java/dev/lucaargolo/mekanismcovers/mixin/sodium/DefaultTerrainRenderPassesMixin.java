package dev.lucaargolo.mekanismcovers.mixin.sodium;

import dev.lucaargolo.mekanismcovers.compat.CompatSodium;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.stream.Stream;

@Mixin(value = DefaultTerrainRenderPasses.class, remap = false)
public class DefaultTerrainRenderPassesMixin {

    @Shadow @Final @Mutable public static TerrainRenderPass[] ALL;

    @Inject(at = @At("TAIL"), method = "<clinit>")
    private static void onInit(CallbackInfo ci) {
        ALL = Stream.concat(Arrays.stream(ALL), Stream.of(CompatSodium.COVER_RENDER_PASS)).toArray(TerrainRenderPass[]::new);
    }

}
