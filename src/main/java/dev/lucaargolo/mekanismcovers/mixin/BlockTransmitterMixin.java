package dev.lucaargolo.mekanismcovers.mixin;

import dev.lucaargolo.mekanismcovers.MekanismCovers;
import dev.lucaargolo.mekanismcovers.mixed.TileEntityTransmitterMixed;
import mekanism.common.block.BlockMekanism;
import mekanism.common.block.states.IStateFluidLoggable;
import mekanism.common.block.transmitter.BlockTransmitter;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.util.WorldUtils;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = BlockTransmitter.class, remap = false)
public abstract class BlockTransmitterMixin extends BlockMekanism implements IStateFluidLoggable {

    @Shadow protected abstract VoxelShape getRealShape(BlockGetter world, BlockPos pos);

    protected BlockTransmitterMixin(Properties properties) {
        super(properties);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lmekanism/common/util/WorldUtils;dismantleBlock(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"), method = "use", locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void getCoverWrenchUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir, ItemStack stack) {
        BlockEntity tile = world.getBlockEntity(pos);
        if(tile instanceof TileEntityTransmitterMixed transmitter && transmitter.mekanism_covers$getCoverState() != null) {
            BlockState coverState = transmitter.mekanism_covers$getCoverState();
            ItemStack currentStack = new ItemStack(MekanismCovers.COVER.get());
            currentStack.getOrCreateTag().putString("CoverState", BlockStateParser.serialize(coverState));
            Containers.dropItemStack(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, currentStack);
            transmitter.mekanism_covers$setCoverState(null);
            tile.setChanged();
            world.sendBlockUpdated(pos, state, state, 3);
            world.getLightEngine().checkBlock(pos);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    @Inject(at = @At("HEAD"), method = "getShape", cancellable = true)
    public void getCorrectShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if(context instanceof EntityCollisionContextAccessor accessor && accessor.getHeldItem().is(MekanismItems.CONFIGURATOR.get()) && MekanismItems.CONFIGURATOR.get().getMode(accessor.getHeldItem()) == ItemConfigurator.ConfiguratorMode.valueOf("COVER")) {
            cir.setReturnValue(getRealShape(world, pos));
        }
    }


    @Inject(at = @At("HEAD"), method = "getRealShape", cancellable = true)
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
