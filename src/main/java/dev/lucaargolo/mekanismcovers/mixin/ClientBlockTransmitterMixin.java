package dev.lucaargolo.mekanismcovers.mixin;

import dev.lucaargolo.mekanismcovers.MekanismCoversClient;
import dev.lucaargolo.mekanismcovers.mixed.TileEntityTransmitterMixed;
import mekanism.common.block.transmitter.BlockTransmitter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockTransmitter.class)
public class ClientBlockTransmitterMixin {

    @Inject(method = "getOcclusionShape", at = @At("RETURN"), cancellable = true)
    private void wrapOcclusion(BlockState state, BlockGetter world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if (MekanismCoversClient.isCoverTransparentFast()) {
            cir.setReturnValue(Shapes.empty());
            return;
        }
        if (state.getBlock() instanceof BlockTransmitter && world.getBlockEntity(pos) instanceof TileEntityTransmitterMixed transmitter) {
            if (transmitter.mekanism_covers$getCoverState() != null) {
                var model = Minecraft.getInstance().getBlockRenderer().getBlockModel(transmitter.mekanism_covers$getCoverState());
                ChunkRenderTypeSet renderTypes = model.getRenderTypes(transmitter.mekanism_covers$getCoverState(), new XoroshiroRandomSource(123456789), ModelData.EMPTY);
                if (renderTypes.contains(RenderType.translucent()) || renderTypes.contains(RenderType.CUTOUT) || renderTypes.contains(RenderType.CUTOUT_MIPPED)) {
                    cir.setReturnValue(Shapes.empty());
                }
            }
        }
    }
}
