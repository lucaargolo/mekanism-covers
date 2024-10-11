package dev.lucaargolo.mekanismcovers.mixin.iris;

import dev.lucaargolo.mekanismcovers.MekanismCoversClient;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.uniforms.CommonUniforms;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CommonUniforms.class, remap = false)
public class CommonUniformsMixin {

    @Inject(at = @At("TAIL"), method = "generalCommonUniforms")
    private static void addCoverTransparencyUniform(UniformHolder uniforms, FrameUpdateNotifier updateNotifier, PackDirectives directives, CallbackInfo ci) {
        uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, "mkcv_CoverTransparency", () -> MekanismCoversClient.isCoverTransparentFast() ? 0.333f : 1.0f);
    }

}
