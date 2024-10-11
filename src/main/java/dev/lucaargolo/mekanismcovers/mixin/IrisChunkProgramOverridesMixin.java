package dev.lucaargolo.mekanismcovers.mixin;

import dev.lucaargolo.mekanismcovers.MekanismCoversClient;
import me.jellysquid.mods.sodium.client.gl.shader.GlProgram;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.IrisChunkProgramOverrides;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.IrisChunkShaderInterface;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.IrisTerrainPass;
import net.irisshaders.iris.pipeline.SodiumTerrainPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = IrisChunkProgramOverrides.class, remap = false)
public abstract class IrisChunkProgramOverridesMixin {

    @Unique
    private boolean mekanism_covers$isTranslucentPass = false;

    @Inject(at = @At("HEAD"), method = "createShader")
    public void createCoverShaderHead(IrisTerrainPass pass, SodiumTerrainPipeline pipeline, ChunkVertexType vertexType, CallbackInfoReturnable<GlProgram<IrisChunkShaderInterface>> cir) {
        if(pass == IrisTerrainPass.GBUFFER_TRANSLUCENT) {
            mekanism_covers$isTranslucentPass = true;
        }
    }

    @Inject(at = @At("TAIL"), method = "createShader")
    public void createCoverShaderTail(IrisTerrainPass pass, SodiumTerrainPipeline pipeline, ChunkVertexType vertexType, CallbackInfoReturnable<GlProgram<IrisChunkShaderInterface>> cir) {
        if(pass == IrisTerrainPass.GBUFFER_TRANSLUCENT) {
            mekanism_covers$isTranslucentPass = false;
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/pipeline/SodiumTerrainPipeline;getTranslucentVertexShaderSource()Ljava/util/Optional;"), method = "createVertexShader")
    public Optional<String> doBadVertexThings(SodiumTerrainPipeline instance) {
        Optional<String> optional = instance.getTranslucentVertexShaderSource();
        if(optional.isPresent() && mekanism_covers$isTranslucentPass) {
            String source = optional.get();
            return Optional.of(MekanismCoversClient.modifyIrisVertex(source));
        }
        return optional;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/pipeline/SodiumTerrainPipeline;getTranslucentFragmentShaderSource()Ljava/util/Optional;"), method = "createFragmentShader")
    public Optional<String> doBadFragmentThings(SodiumTerrainPipeline instance) {
        Optional<String> optional = instance.getTranslucentFragmentShaderSource();
        if(optional.isPresent() && mekanism_covers$isTranslucentPass) {
            String source = optional.get();
            return Optional.of(MekanismCoversClient.modifyIrisFragment(source));
        }
        return optional;
    }


}
