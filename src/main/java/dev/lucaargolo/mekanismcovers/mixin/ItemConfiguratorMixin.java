package dev.lucaargolo.mekanismcovers.mixin;

import dev.lucaargolo.mekanismcovers.MekanismCovers;
import dev.lucaargolo.mekanismcovers.mixed.TileEntityTransmitterMixed;
import mekanism.common.item.ItemConfigurator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = ItemConfigurator.class)
public class ItemConfiguratorMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lmekanism/common/item/ItemConfigurator$ConfiguratorMode;isConfigurating()Z", remap = false), method = "useOn", cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    public void coverModeUse(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, Player player, Level level, BlockPos pos, Direction side, ItemStack stack, BlockEntity tile, ItemConfigurator.ConfiguratorMode mode) {
        if(mode == ItemConfigurator.ConfiguratorMode.valueOf("COVER")) {
            if(tile instanceof TileEntityTransmitterMixed transmitter && transmitter.mekanism_covers$getCoverState() != null) {
                BlockState state = level.getBlockState(pos);
                MekanismCovers.removeCover(level, tile, state, pos, transmitter);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

}
