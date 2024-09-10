package dev.lucaargolo.mekanismcovers.mixin;

import dev.lucaargolo.mekanismcovers.mixed.TileEntityTransmitterMixed;
import mekanism.common.block.transmitter.BlockTransmitter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

    @Shadow public abstract Block getBlock();

    @Inject(at = @At("HEAD"), method = "getTicker", cancellable = true)
    public  <T extends BlockEntity> void addClientTickerToTransmitters(Level pLevel, BlockEntityType<T> pBlockEntityType, CallbackInfoReturnable<BlockEntityTicker<T>> cir) {
        if(this.getBlock() instanceof BlockTransmitter && pLevel.isClientSide) {
            cir.setReturnValue((pLevel1, pPos, pState, pBlockEntity) -> {
                if(pBlockEntity instanceof TileEntityTransmitterMixed transmitter) {
                    transmitter.mekanism_covers$onUpdateClient();
                }
            });

        }
    }

}
