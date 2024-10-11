package dev.lucaargolo.mekanismcovers.mixin.iris;

import dev.lucaargolo.mekanismcovers.MekanismCoversClient;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.block.transmitter.BlockTransmitter;
import mekanism.common.registries.MekanismBlocks;
import net.irisshaders.iris.shaderpack.materialmap.BlockEntry;
import net.irisshaders.iris.shaderpack.materialmap.BlockMaterialMapping;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(value = BlockMaterialMapping.class, remap = false)
public abstract class BlockMaterialMappingMixin {

    @Shadow
    private static void addBlockStates(BlockEntry entry, Object2IntMap<BlockState> idMap, int intId) {
        throw new AssertionError();
    }

    @Inject(at = @At("TAIL"), method = "createBlockStateIdMap", locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void captureLastEmptyId(Int2ObjectMap<List<BlockEntry>> blockPropertiesMap, CallbackInfoReturnable<Object2IntMap<BlockState>> cir, Object2IntMap<BlockState> blockStateIds) {
        boolean found = false;
        for (short i = Short.MAX_VALUE; i > Short.MIN_VALUE; i--) {
            if(!blockPropertiesMap.containsKey(i)) {
                MekanismCoversClient.COVER_ENTITY_ID = i;
                found = true;
                break;
            }
        }
        if(!found) {
            MekanismCoversClient.COVER_ENTITY_ID = null;
        }else{
            MekanismBlocks.BLOCKS.getAllBlocks().stream()
                .filter(holder -> holder.getBlock() instanceof BlockTransmitter)
                .map(IBlockProvider::getRegistryName)
                .forEach(location -> {
                    addBlockStates(BlockEntry.parse(location.toString()), blockStateIds, MekanismCoversClient.COVER_ENTITY_ID);
                });
        }

    }

}
