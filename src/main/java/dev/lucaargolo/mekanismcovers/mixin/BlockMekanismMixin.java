package dev.lucaargolo.mekanismcovers.mixin;

import dev.lucaargolo.mekanismcovers.MekanismCovers;
import dev.lucaargolo.mekanismcovers.mixed.TileEntityTransmitterMixed;
import mekanism.common.block.BlockMekanism;
import mekanism.common.block.transmitter.BlockTransmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockMekanism.class)
public class BlockMekanismMixin {

    @SuppressWarnings("ConstantValue")
    @Inject(at = @At("HEAD"), method = "onRemove")
    public void dropCoverOnRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving, CallbackInfo ci) {
        if((!state.is(newState.getBlock()) || !newState.getFluidState().isEmpty()) && (Object) this instanceof BlockTransmitter) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof TileEntityTransmitterMixed transmitter && transmitter.mekanism_covers$getCoverState() != null) {
                MekanismCovers.removeCover(world, blockEntity, state, pos, transmitter, false);
            }
        }
    }

}
