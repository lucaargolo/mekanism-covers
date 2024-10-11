package dev.lucaargolo.mekanismcovers.mixin;

import dev.lucaargolo.mekanismcovers.MekanismCoversClient;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import mekanism.common.block.transmitter.BlockTransmitter;
import net.irisshaders.iris.shaderpack.materialmap.BlockEntry;
import net.irisshaders.iris.shaderpack.materialmap.BlockMaterialMapping;
import net.irisshaders.iris.shaderpack.materialmap.TagEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Objects;

@Mixin(value = BlockMaterialMapping.class, remap = false)
public abstract class BlockMaterialMappingMixin {

    @Shadow
    private static void addBlockStates(BlockEntry entry, Object2IntMap<BlockState> idMap, int intId) {
        throw new AssertionError();
    }

    @SuppressWarnings("deprecation")
    @Inject(at = @At("TAIL"), method = "createBlockStateIdMap", locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void captureLastEmptyId(Int2ObjectLinkedOpenHashMap<List<BlockEntry>> blockPropertiesMap, Int2ObjectLinkedOpenHashMap<List<TagEntry>> tagPropertiesMap, CallbackInfoReturnable<Object2IntMap<BlockState>> cir, Object2IntMap<BlockState> blockStateIds) {
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
            BuiltInRegistries.BLOCK.stream()
                .filter(block -> block instanceof BlockTransmitter && block.builtInRegistryHolder().getKey() != null)
                .map(block -> Objects.requireNonNull(block.builtInRegistryHolder().getKey()).location())
                .forEach(location -> addBlockStates((BlockEntry) BlockEntry.parse(location.toString()), blockStateIds, MekanismCoversClient.COVER_ENTITY_ID));
        }

    }

}
