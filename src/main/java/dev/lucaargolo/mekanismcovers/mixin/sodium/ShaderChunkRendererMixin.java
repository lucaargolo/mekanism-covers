package dev.lucaargolo.mekanismcovers.mixin.sodium;

import dev.lucaargolo.mekanismcovers.MekanismCoversClient;
import dev.lucaargolo.mekanismcovers.sodium.CustomTerrainRenderPasses;
import me.jellysquid.mods.sodium.client.gl.shader.GlProgram;
import me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniformFloat;
import me.jellysquid.mods.sodium.client.render.chunk.ShaderChunkRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderInterface;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderOptions;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = ShaderChunkRenderer.class, remap = false)
public abstract class ShaderChunkRendererMixin {

    @Shadow @Final private Map<ChunkShaderOptions, GlProgram<ChunkShaderInterface>> programs;
    @Shadow protected abstract GlProgram<ChunkShaderInterface> createShader(String path, ChunkShaderOptions options);
    @Unique
    private GlUniformFloat mekanism_covers$coverTransparencyUniform = null;

    @Inject(at = @At("HEAD"), method = "compileProgram", cancellable = true)
    public void compileCoverProgram(ChunkShaderOptions options, CallbackInfoReturnable<GlProgram<ChunkShaderInterface>> cir) {
        if(options.pass() == CustomTerrainRenderPasses.COVER) {
            GlProgram<ChunkShaderInterface> program = this.programs.get(options);
            if(program == null) {
                this.programs.put(options, program = this.createShader("blocks/cover_block_layer_opaque", options));
                try {
                    mekanism_covers$coverTransparencyUniform = program.bindUniform("u_CoverTransparency", GlUniformFloat::new);
                }catch (Exception e) {
                    mekanism_covers$coverTransparencyUniform = null;
                }
            }
            cir.setReturnValue(program);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/shader/ChunkShaderInterface;setupState()V"), method = "begin")
    public void updateCoverUniform(TerrainRenderPass pass, CallbackInfo ci) {
        if(pass == CustomTerrainRenderPasses.COVER) {
            if(mekanism_covers$coverTransparencyUniform != null) {
                mekanism_covers$coverTransparencyUniform.set(MekanismCoversClient.getTransparency());
            }
        }
    }


}
