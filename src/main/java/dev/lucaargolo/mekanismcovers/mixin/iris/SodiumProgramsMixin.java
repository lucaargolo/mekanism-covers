package dev.lucaargolo.mekanismcovers.mixin.iris;

import dev.lucaargolo.mekanismcovers.compat.CompatSodium;
import net.caffeinemc.mods.sodium.client.gl.shader.GlShader;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.irisshaders.iris.pipeline.programs.SodiumPrograms;
import net.irisshaders.iris.pipeline.transform.PatchShaderType;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(SodiumPrograms.class)
public class SodiumProgramsMixin {

    @Inject(at = @At("HEAD"), method = "mapTerrainRenderPass", cancellable = true)
    public void addCoverToTerrainPass(TerrainRenderPass pass, CallbackInfoReturnable<SodiumPrograms.Pass> cir) {
        if(pass == CompatSodium.COVER_RENDER_PASS) {
            cir.setReturnValue(ShadowRenderingState.areShadowsCurrentlyBeingRendered() ? SodiumPrograms.Pass.SHADOW_TRANS : SodiumPrograms.Pass.valueOf("COVER"));
        }
    }


    @Inject(at = @At("HEAD"), method = "createGlShaders")
    public void createCoverShader(String passName, Map<PatchShaderType, String> transformed, CallbackInfoReturnable<Map<PatchShaderType, GlShader>> cir) {
        if(passName.equals("cover")) {
            transformed.put(PatchShaderType.FRAGMENT, mekanism_covers$modifyShader(transformed.get(PatchShaderType.FRAGMENT)));
        }
    }

    @Unique
    private static String mekanism_covers$modifyShader(String source) {
        String[] lines = source.split("\n");

        StringBuilder modifiedSource = new StringBuilder();
        modifiedSource.append(lines[0]).append("\n");
        modifiedSource.append("uniform float mkcv_CoverTransparency;\n");
        for (int i = 1; i < lines.length - 1; i++) {
            modifiedSource.append(lines[i]).append("\n");
        }
        modifiedSource.append("iris_FragData0.a = min(mkcv_CoverTransparency, iris_FragData0.a);\n");
        modifiedSource.append(lines[lines.length - 1]);

        return modifiedSource.toString();
    }

}
