package dev.lucaargolo.mekanismcovers.mixin;

import dev.lucaargolo.mekanismcovers.MekanismCovers;
import dev.lucaargolo.mekanismcovers.mixed.TileEntityTransmitterMixed;
import mekanism.common.block.BlockMekanism;
import mekanism.common.block.transmitter.BlockTransmitter;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockMekanism.class, remap = false)
public class BlockMekanismMixin {

    @SuppressWarnings("ConstantValue")
    @Inject(at = @At("HEAD"), method = "onRemove")
    public void dropCoverOnRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving, CallbackInfo ci) {
        if((!state.is(newState.getBlock()) || !newState.getFluidState().isEmpty()) && (Object) this instanceof BlockTransmitter) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof TileEntityTransmitterMixed transmitter && transmitter.mekanism_covers$getCoverState() != null) {
                BlockState coverState = transmitter.mekanism_covers$getCoverState();
                ItemStack stack = new ItemStack(MekanismCovers.COVER.get());
                stack.getOrCreateTag().putString("CoverState", BlockStateParser.serialize(coverState));
                Containers.dropItemStack(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, stack);
                transmitter.mekanism_covers$setCoverState(null);
                blockEntity.setChanged();
            }
        }
    }

}
