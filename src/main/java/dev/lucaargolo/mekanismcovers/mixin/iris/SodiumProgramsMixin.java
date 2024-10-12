package dev.lucaargolo.mekanismcovers.mixin.iris;

import dev.lucaargolo.mekanismcovers.MekanismCoversClient;
import net.caffeinemc.mods.sodium.client.gl.shader.GlShader;
import net.irisshaders.iris.pipeline.programs.SodiumPrograms;
import net.irisshaders.iris.pipeline.transform.PatchShaderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(SodiumPrograms.class)
public class SodiumProgramsMixin {


    @Inject(at = @At("HEAD"), method = "createGlShaders")
    public void createCoverShader(String passName, Map<PatchShaderType, String> transformed, CallbackInfoReturnable<Map<PatchShaderType, GlShader>> cir) {
        if(passName.equals("translucent")) {
            transformed.put(PatchShaderType.VERTEX, MekanismCoversClient.modifyIrisVertex(transformed.get(PatchShaderType.VERTEX)));
            transformed.put(PatchShaderType.FRAGMENT, MekanismCoversClient.modifyIrisFragment(transformed.get(PatchShaderType.FRAGMENT)));
        }
    }
}
