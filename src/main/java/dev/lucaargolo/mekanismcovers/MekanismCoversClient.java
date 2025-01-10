package dev.lucaargolo.mekanismcovers;

import dev.lucaargolo.mekanismcovers.mixed.TileEntityTransmitterMixed;
import mekanism.common.block.transmitter.BlockTransmitter;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Optional;

import static dev.lucaargolo.mekanismcovers.MekanismCovers.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MekanismCoversClient {

    public static final boolean ADVANCED_COVER_RENDERING = !ModConfig.getInstance().isDisableAdvancedCoverRendering();
    public static final boolean SHADER_COVER_RENDERING = ModConfig.getInstance().isEnableShaderCompatibleRendering();

    private static boolean lastTransparency = false;

    public static final ModelResourceLocation COVER_MODEL = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(MODID, "block/cover"));

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new CoverItem.CoverItemExtensions(), MekanismCovers.COVER.get());
    }

    @SubscribeEvent
    public static void registerCoverModel(ModelEvent.RegisterAdditional event) {
        event.register(COVER_MODEL);
    }

    @SubscribeEvent
    public static void blockColorsRegister(RegisterColorHandlersEvent.Block event) {
        Block[] transmitters = BuiltInRegistries.BLOCK.stream().filter(block -> block instanceof BlockTransmitter).toList().toArray(new Block[0]);
        event.register((pState, pLevel, pPos, pTintIndex) -> {
            if(pTintIndex == 1337 && pPos != null) {
                TileEntityTransmitter tile = WorldUtils.getTileEntity(TileEntityTransmitter.class, pLevel, pPos);
                if(tile instanceof TileEntityTransmitterMixed transmitter) {
                    BlockState coverState = transmitter.mekanism_covers$getCoverState();
                    if(coverState != null) {
                        return event.getBlockColors().getColor(coverState, pLevel, pPos, pTintIndex);
                    }
                }
            }
            return 0xFFFFFF;
        }, transmitters);
    }

    public static void updateCoverTransparency() {
        boolean transparency = isCoverTransparent();
        if(transparency != lastTransparency) {
            var client = Minecraft.getInstance();
            if (client.player == null || client.level == null) {
                return;
            }

            var viewDistance = (int) Math.ceil(client.levelRenderer.getLastViewDistance());
            ChunkPos.rangeClosed(client.player.chunkPosition(), viewDistance).forEach(chunkPos -> {
                var chunk = client.level.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
                if (chunk != null) {
                    for (var i = 0; i < chunk.getSectionsCount(); i++) {
                        var section = chunk.getSection(i);
                        if (section.maybeHas(state -> state.getBlock() instanceof BlockTransmitter)) {
                            client.levelRenderer.setSectionDirty(chunkPos.x, chunk.getSectionYFromSectionIndex(i), chunkPos.z);
                        }
                    }
                }
            });
        }
        lastTransparency = transparency;

    }

    public static boolean isCoverTransparentFast() {
        return lastTransparency;
    }

    private static boolean isCoverTransparent() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack mainStack = player.getMainHandItem();
            ItemStack offStack = player.getOffhandItem();
            ItemStack[] stacks = new ItemStack[] { mainStack, offStack };
            boolean transparent = false;
            for (ItemStack stack : stacks) {
                if(stack.is(MekanismItems.CONFIGURATOR.get())) {
                    ItemConfigurator.ConfiguratorMode mode = MekanismItems.CONFIGURATOR.get().getMode(mainStack);
                    if(mode != ItemConfigurator.ConfiguratorMode.WRENCH) {
                        transparent = true;
                        break;
                    }
                }else {
                    Item item = stack.getItem();
                    if(item instanceof BlockItem blockItem) {
                        Block block = blockItem.getBlock();
                        if(block instanceof BlockTransmitter) {
                            transparent = true;
                            break;
                        }
                    }
                }
            }
            return transparent;
        } else {
            return false;
        }
    }

    @NotNull
    public static ModelData getModelData(BlockState state, BlockAndTintGetter level, BlockPos worldPosition) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(state).getModelData(level, worldPosition, state, ModelData.EMPTY);
    }

    @SuppressWarnings("rawtypes")
    public static boolean hasShaderPack() {
        try {
            Class<?> irisClass = Thread.currentThread().getContextClassLoader().loadClass("net.irisshaders.iris.Iris");
            Method packMethod = irisClass.getDeclaredMethod("getCurrentPack");
            Optional optional = (Optional) packMethod.invoke(null);
            return optional.isPresent();
        }catch (Exception ignored) {
            return false;
        }
    }

}
