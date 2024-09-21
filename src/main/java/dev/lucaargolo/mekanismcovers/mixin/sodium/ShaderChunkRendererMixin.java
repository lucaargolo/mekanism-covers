package dev.lucaargolo.mekanismcovers.mixin.sodium;

import dev.lucaargolo.mekanismcovers.MekanismCoversClient;
import dev.lucaargolo.mekanismcovers.compat.CompatSodium;

import net.caffeinemc.mods.sodium.client.gl.shader.*;
import net.caffeinemc.mods.sodium.client.gl.shader.uniform.GlUniformFloat;
import net.caffeinemc.mods.sodium.client.render.chunk.ShaderChunkRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ChunkShaderInterface;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ChunkShaderOptions;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
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
    @Unique
    private GlUniformFloat mekanism_covers$coverTransparencyUniform = null;


    @Inject(at = @At("HEAD"), method = "compileProgram")
    public void compileProgramHead(ChunkShaderOptions options, CallbackInfoReturnable<GlProgram<ChunkShaderInterface>> cir) {
        if(options.pass() == CompatSodium.COVER_RENDER_PASS) {
            GlProgram<ChunkShaderInterface> program = this.programs.get(options);
            if(program == null) {
                CompatSodium.IS_COVER_RENDER_PASS = true;
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "compileProgram")
    public void compileProgramTail(ChunkShaderOptions options, CallbackInfoReturnable<GlProgram<ChunkShaderInterface>> cir) {
        if(CompatSodium.IS_COVER_RENDER_PASS) {
            GlProgram<ChunkShaderInterface> program = this.programs.get(options);
            if(program != null) {
                try {
                    mekanism_covers$coverTransparencyUniform = program.bindUniform("u_CoverTransparency", GlUniformFloat::new);
                }catch (Exception e) {
                    mekanism_covers$coverTransparencyUniform = null;
                }
            }
        }
        CompatSodium.IS_COVER_RENDER_PASS = false;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/shader/ChunkShaderInterface;setupState()V"), method = "begin")
    public void updateCoverUniform(TerrainRenderPass pass, CallbackInfo ci) {
        if(pass == CompatSodium.COVER_RENDER_PASS) {
            if(mekanism_covers$coverTransparencyUniform != null) {
                mekanism_covers$coverTransparencyUniform.set(MekanismCoversClient.getShaderTransparency());
            }
        }
    }


}
