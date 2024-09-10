package dev.lucaargolo.mekanismcovers.mixin;

import dev.lucaargolo.mekanismcovers.MekanismCovers;
import dev.lucaargolo.mekanismcovers.mixed.TileEntityTransmitterMixed;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.block.states.FluidLogType;
import mekanism.common.item.ItemConfigurator;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
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

@Mixin(value = ItemConfigurator.class, remap = false)
public class ItemConfiguratorMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lmekanism/common/item/ItemConfigurator$ConfiguratorMode;isConfigurating()Z"), method = "useOn", cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    public void coverModeUse(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, Player player, Level level, BlockPos pos, Direction side, ItemStack stack, BlockEntity tile, ItemConfigurator.ConfiguratorMode mode) {
        if(mode == ItemConfigurator.ConfiguratorMode.valueOf("COVER")) {
            if(tile instanceof TileEntityTransmitterMixed transmitter && transmitter.mekanism_covers$getCoverState() != null) {
                BlockState state = level.getBlockState(pos);
                BlockState coverState = transmitter.mekanism_covers$getCoverState();
                ItemStack currentStack = new ItemStack(MekanismCovers.COVER.get());
                currentStack.getOrCreateTag().putString("CoverState", BlockStateParser.serialize(coverState));
                Containers.dropItemStack(level, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, currentStack);
                transmitter.mekanism_covers$setCoverState(null);
                tile.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
                level.getLightEngine().checkBlock(pos);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

}
