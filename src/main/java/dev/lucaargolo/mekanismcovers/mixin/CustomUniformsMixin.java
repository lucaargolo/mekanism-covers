package dev.lucaargolo.mekanismcovers.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.irisshaders.iris.uniforms.custom.cached.CachedUniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Set;

@Mixin(value = CustomUniforms.class, remap = false)
public class CustomUniformsMixin {

    @Inject(at = @At(value = "INVOKE", target = "Ljava/util/List;removeAll(Ljava/util/Collection;)Z"), method = "optimise", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void doNotRemoveCoverUniform(CallbackInfo ci, Object2IntMap<CachedUniform> dependedByCount, Set<CachedUniform> unused) {
        unused.removeIf(uniform -> uniform.getName().startsWith("mkcv"));
    }

}
