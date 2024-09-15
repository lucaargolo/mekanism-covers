package dev.lucaargolo.mekanismcovers.mixin;

import dev.lucaargolo.mekanismcovers.mixed.TileEntityTransmitterMixed;
import mekanism.common.block.BlockMekanism;
import mekanism.common.block.states.IStateFluidLoggable;
import mekanism.common.block.transmitter.BlockTransmitter;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockTransmitter.class)
public abstract class BlockTransmitterMixin extends BlockMekanism implements IStateFluidLoggable {

    @Shadow(remap = false) protected abstract VoxelShape getRealShape(BlockGetter world, BlockPos pos);

    protected BlockTransmitterMixin(Properties properties) {
        super(properties);
    }

    @Inject(at = @At("HEAD"), method = "getShape", cancellable = true)
    public void getCorrectShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if(context instanceof EntityCollisionContextAccessor accessor && accessor.getHeldItem() != null && accessor.getHeldItem().is(MekanismItems.CONFIGURATOR.get()) && MekanismItems.CONFIGURATOR.get().getMode(accessor.getHeldItem()) == ItemConfigurator.ConfiguratorMode.valueOf("COVER")) {
            cir.setReturnValue(getRealShape(world, pos));
        }
    }


    @Inject(at = @At("HEAD"), method = "getRealShape", cancellable = true, remap = false)
    public void getCoverShape(BlockGetter world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        TileEntityTransmitter tile = WorldUtils.getTileEntity(TileEntityTransmitter.class, world, pos);
        if(tile instanceof TileEntityTransmitterMixed transmitter && transmitter.mekanism_covers$getCoverState() != null) {
            cir.setReturnValue(Shapes.block());
        }
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        int ambientLight = super.getLightEmission(state, world, pos);
        if (ambientLight != 15) {
            TileEntityTransmitter tile = WorldUtils.getTileEntity(TileEntityTransmitter.class, world, pos);
            if (tile instanceof TileEntityTransmitterMixed transmitter) {
                BlockState coverState = transmitter.mekanism_covers$getCoverState();
                if (coverState != null) {
                    ambientLight = Math.max(ambientLight, coverState.getLightEmission(world, pos));
                }
            }
        }
        return ambientLight;
    }
}
