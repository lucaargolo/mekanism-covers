package dev.lucaargolo.mekanismcovers.mixin;

import dev.lucaargolo.mekanismcovers.MekanismCovers;
import dev.lucaargolo.mekanismcovers.mixed.TileEntityTransmitterMixed;
import mekanism.api.IAlloyInteraction;
import mekanism.common.capabilities.proxy.ProxyConfigurable;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.CapabilityTileEntity;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityTransmitter.class)
public abstract class TileEntityTransmitterMixin extends CapabilityTileEntity implements ProxyConfigurable.ISidedConfigurable, IAlloyInteraction, TileEntityTransmitterMixed {

    @Unique
    private BlockState mekanism_covers$coverState = null;
    @Unique
    private boolean mekanism_covers$updateClientLight = false;

    public TileEntityTransmitterMixin(TileEntityTypeRegistryObject<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(at = @At("RETURN"), method = "getModelData", cancellable = true, remap = false)
    public void injectCoverModel(CallbackInfoReturnable<ModelData> cir) {
        ModelData.Builder builder = cir.getReturnValue().derive();
        if(this.mekanism_covers$coverState != null) {
            builder.with(MekanismCovers.COVER_STATE, this.mekanism_covers$coverState);
        }
        cir.setReturnValue(builder.build());
    }

    @Inject(at = @At("TAIL"), method = "saveAdditional")
    public void injectSaveCover(CompoundTag nbtTags, HolderLookup.Provider provider, CallbackInfo ci) {
        if(this.mekanism_covers$coverState != null) {
            nbtTags.putString("CoverState", BlockStateParser.serialize(this.mekanism_covers$coverState));
        }
    }

    @Inject(at = @At("TAIL"), method = "loadAdditional")
    public void injectLoad(CompoundTag nbt, HolderLookup.Provider provider, CallbackInfo ci) {
        try {
            String serialized = nbt.getString("CoverState");
            BlockStateParser.BlockResult result = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), serialized, false);
            this.mekanism_covers$coverState = result.blockState();
            if(this.level != null) {
                AuxiliaryLightManager lightManager = this.level.getAuxLightManager(this.worldPosition);
                if(lightManager != null) {
                    lightManager.setLightAt(this.worldPosition, mekanism_covers$coverState != null ? mekanism_covers$coverState.getLightEmission(this.level, this.worldPosition) : 0);
                }
                this.level.getLightEngine().checkBlock(this.worldPosition);
            }
        }catch (Exception exception) {
            this.mekanism_covers$coverState = null;
        }
    }

    @Inject(at = @At("RETURN"), method = "getReducedUpdateTag", cancellable = true, remap = false)
    public void injectSaveCover(CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag nbtTags = cir.getReturnValue();
        if(this.mekanism_covers$coverState != null) {
            nbtTags.putString("CoverState", BlockStateParser.serialize(this.mekanism_covers$coverState));
        }
        cir.setReturnValue(nbtTags);
    }

    @Inject(at = @At("TAIL"), method = "handleUpdateTag", remap = false)
    public void injectUpdateTag(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
        try {
            String serialized = tag.getString("CoverState");
            BlockStateParser.BlockResult result = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), serialized, false);
            this.mekanism_covers$coverState = result.blockState();
            MekanismCovers.POSSIBLE_BLOCKS.put(this.worldPosition, this.mekanism_covers$coverState);
            this.mekanism_covers$updateClientLight = true;
        }catch (Exception exception) {
            this.mekanism_covers$coverState = null;
            MekanismCovers.POSSIBLE_BLOCKS.remove(this.worldPosition);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lmekanism/common/content/network/transmitter/Transmitter;setTransmitterNetwork(Lmekanism/common/lib/transmitter/DynamicNetwork;)V"), method = "onWorldSeparate")
    public void injectUnload(boolean stillPresent, CallbackInfo ci) {
        MekanismCovers.POSSIBLE_BLOCKS.remove(this.worldPosition);
    }

    @Override
    public void mekanism_covers$onUpdateClient() {
        if (this.mekanism_covers$updateClientLight) {
            if(this.level != null) {
                AuxiliaryLightManager lightManager = this.level.getAuxLightManager(this.worldPosition);
                if(lightManager != null) {
                    lightManager.setLightAt(this.worldPosition, mekanism_covers$coverState != null ? mekanism_covers$coverState.getLightEmission(this.level, this.worldPosition) : 0);
                }
                this.level.getLightEngine().checkBlock(this.worldPosition);
                this.mekanism_covers$updateClientLight = !this.level.getLightEngine().lightOnInSection(SectionPos.of(this.worldPosition));
            }
        }
    }

    @Override
    public BlockState mekanism_covers$getCoverState() {
        return this.mekanism_covers$coverState;
    }

    @Override
    public void mekanism_covers$setCoverState(BlockState coverState) {
        this.mekanism_covers$coverState = coverState;
    }
}
