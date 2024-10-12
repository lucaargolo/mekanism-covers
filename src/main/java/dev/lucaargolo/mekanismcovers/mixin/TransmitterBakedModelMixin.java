package dev.lucaargolo.mekanismcovers.mixin;

import dev.lucaargolo.mekanismcovers.MekanismCovers;
import dev.lucaargolo.mekanismcovers.MekanismCoversClient;
import mekanism.client.render.obj.TransmitterBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(value = TransmitterBakedModel.class, remap = false)
public class TransmitterBakedModelMixin extends BakedModelWrapper<BakedModel> {

    public TransmitterBakedModelMixin(BakedModel originalModel) {
        super(originalModel);
    }

    @Inject(at = @At("RETURN"), method = "getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;", cancellable = true)
    public void injectCoverModel(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType, CallbackInfoReturnable<List<BakedQuad>> cir) {
        List<BakedQuad> originalQuads = cir.getReturnValue();
        if(extraData.has(MekanismCovers.COVER_STATE)) {
            Minecraft minecraft = Minecraft.getInstance();
            BlockState coverState = extraData.get(MekanismCovers.COVER_STATE);
            if(coverState != null) {
                BakedModel bakedModel = minecraft.getBlockRenderer().getBlockModel(coverState);
                boolean transparent = MekanismCoversClient.isCoverTransparentFast();
                if(transparent) {
                    if(renderType == RenderType.translucent()) {
                        if(MekanismCoversClient.ADVANCED_COVER_RENDERING) {
                            List<BakedQuad> coverQuads = bakedModel.getQuads(coverState, side, rand, extraData, renderType);
                            coverQuads.forEach(q -> ((BakedQuadAccessor) q).setTintIndex(1337));
                            cir.setReturnValue(Stream.concat(originalQuads.stream(), coverQuads.stream()).toList());
                        }else{
                            BakedModel altModel = minecraft.getModelManager().getModel(MekanismCovers.COVER_MODEL);
                            List<BakedQuad> altQuads = altModel.getQuads(Blocks.AIR.defaultBlockState(), side, rand, extraData, renderType);
                            cir.setReturnValue(Stream.concat(originalQuads.stream(), altQuads.stream()).toList());
                        }
                    }
                }else{
                    if(renderType != null && bakedModel.getRenderTypes(coverState, rand, ModelData.EMPTY).contains(renderType)) {
                        List<BakedQuad> coverQuads = bakedModel.getQuads(coverState, side, rand, extraData, renderType);
                        coverQuads.forEach(q -> ((BakedQuadAccessor) q).setTintIndex(1337));
                        cir.setReturnValue(Stream.concat(originalQuads.stream(), coverQuads.stream()).toList());
                    }
                }
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "getRenderTypes(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;)Lnet/neoforged/neoforge/client/ChunkRenderTypeSet;", cancellable = true)
    public void injectCoverRenderTypes(BlockState state, RandomSource rand, ModelData extraData, CallbackInfoReturnable<ChunkRenderTypeSet> cir) {
        ChunkRenderTypeSet cableSet = cir.getReturnValue();
        if(extraData.has(MekanismCovers.COVER_STATE)) {
            BlockState coverState = extraData.get(MekanismCovers.COVER_STATE);
            if(coverState != null) {
                boolean transparent = MekanismCoversClient.isCoverTransparentFast();
                if(transparent) {
                    cir.setReturnValue(ChunkRenderTypeSet.of(Stream.concat(cableSet.asList().stream(), Stream.of(RenderType.translucent())).toList().toArray(new RenderType[0])));
                }else{
                    Minecraft minecraft = Minecraft.getInstance();
                    BakedModel bakedModel = minecraft.getBlockRenderer().getBlockModel(coverState);
                    ChunkRenderTypeSet coverSet = bakedModel.getRenderTypes(coverState, rand, ModelData.EMPTY);
                    cir.setReturnValue(ChunkRenderTypeSet.of(Stream.concat(cableSet.asList().stream(), coverSet.asList().stream()).toList().toArray(new RenderType[0])));
                }
            }
        }
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon(@NotNull ModelData extraData) {
        if(extraData.has(MekanismCovers.COVER_STATE)) {
            Minecraft minecraft = Minecraft.getInstance();
            BlockState coverState = extraData.get(MekanismCovers.COVER_STATE);
            if(coverState != null) {
                BakedModel bakedModel = minecraft.getBlockRenderer().getBlockModel(coverState);
                return bakedModel.getParticleIcon(extraData);
            }
        }
        return super.getParticleIcon(extraData);
    }
}
