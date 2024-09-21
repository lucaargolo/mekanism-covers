package dev.lucaargolo.mekanismcovers.mixin.sodium;

import dev.lucaargolo.mekanismcovers.compat.CompatSodium;
import net.caffeinemc.mods.sodium.client.gl.shader.*;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShaderLoader.class)
public class ShaderLoaderMixin {

    @Inject(at = @At("RETURN"), method = "getShaderSource", cancellable = true)
    private static void injectCoverUniform(ResourceLocation name, CallbackInfoReturnable<String> cir) {
        if(CompatSodium.IS_COVER_RENDER_PASS && name.getPath().endsWith(".fsh")) {
            cir.setReturnValue(mekanism_covers$modifyShader(cir.getReturnValue()));
        }
    }

    @Unique
    private static String mekanism_covers$modifyShader(String source) {
        String[] lines = source.split("\n");

        StringBuilder modifiedSource = new StringBuilder();
        modifiedSource.append(lines[0]).append("\n");
        modifiedSource.append("uniform float u_CoverTransparency;\n");
        for (int i = 1; i < lines.length - 1; i++) {
            modifiedSource.append(lines[i]).append("\n");
        }
        modifiedSource.append("fragColor.a = min(u_CoverTransparency, fragColor.a);\n");
        modifiedSource.append(lines[lines.length - 1]);

        return modifiedSource.toString();
    }


}
