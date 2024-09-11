package dev.lucaargolo.mekanismcovers.mixin.iris;

import dev.lucaargolo.mekanismcovers.sodium.CustomTerrainRenderPasses;
import me.jellysquid.mods.sodium.client.gl.shader.GlProgram;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.IrisChunkProgramOverrides;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.IrisChunkShaderInterface;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.IrisTerrainPass;
import net.irisshaders.iris.pipeline.SodiumTerrainPipeline;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumMap;
import java.util.Optional;

@Mixin(value = IrisChunkProgramOverrides.class, remap = false)
public abstract class IrisChunkProgramOverridesMixin {

    @Shadow @Nullable protected abstract GlProgram<IrisChunkShaderInterface> createShader(IrisTerrainPass pass, SodiumTerrainPipeline pipeline, ChunkVertexType vertexType);

    @Shadow @Final private EnumMap<IrisTerrainPass, GlProgram<IrisChunkShaderInterface>> programs;
    @Unique
    private boolean mekanism_covers$isCoverPass = false;

    @Inject(at = @At("RETURN"), method = "getProgramOverride", cancellable = true)
    public void passCoverShaderOverride(TerrainRenderPass pass, ChunkVertexType vertexType, CallbackInfoReturnable<GlProgram<IrisChunkShaderInterface>> cir) {
        if(pass == CustomTerrainRenderPasses.COVER) {
            cir.setReturnValue(this.programs.get(IrisTerrainPass.valueOf("COVER")));
        }
    }

    @Inject(at = @At("HEAD"), method = "createShader", cancellable = true)
    public void createCoverShader(IrisTerrainPass pass, SodiumTerrainPipeline pipeline, ChunkVertexType vertexType, CallbackInfoReturnable<GlProgram<IrisChunkShaderInterface>> cir) {
        if(pass == IrisTerrainPass.valueOf("COVER")) {
            mekanism_covers$isCoverPass = true;
            cir.setReturnValue(createShader(IrisTerrainPass.GBUFFER_TRANSLUCENT, pipeline, vertexType));
            mekanism_covers$isCoverPass = false;
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/pipeline/SodiumTerrainPipeline;getTranslucentFragmentShaderSource()Ljava/util/Optional;"), method = "createFragmentShader")
    public Optional<String> doBadThings(SodiumTerrainPipeline instance) {
        Optional<String> optional = instance.getTranslucentFragmentShaderSource();
        if(optional.isPresent() && mekanism_covers$isCoverPass) {
            String source = optional.get();
            return Optional.of(mekanism_covers$modifyShader(source));
        }
        return optional;
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
