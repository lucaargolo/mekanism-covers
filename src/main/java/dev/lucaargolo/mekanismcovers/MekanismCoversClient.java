package dev.lucaargolo.mekanismcovers;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.lucaargolo.mekanismcovers.mixed.TileEntityTransmitterMixed;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.block.transmitter.BlockTransmitter;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.io.IOException;

import static dev.lucaargolo.mekanismcovers.MekanismCovers.COVER_MODEL;
import static dev.lucaargolo.mekanismcovers.MekanismCovers.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MekanismCoversClient {

    public static boolean DISABLE_ADVANCED_LAYER = ModConfig.getInstance().isDisableAdvancedLayer();
    public static ShaderInstance COVER_SHADER;
    public static Uniform COVER_TRANSPARENCY;

    private static boolean lastTransparency = false;

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new CoverItem.CoverItemExtensions(), MekanismCovers.COVER.get());
    }

    @SubscribeEvent
    public static void registerCoverModel(ModelEvent.RegisterAdditional event) {
        event.register(COVER_MODEL);
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(MODID, "rendertype_cover"), DefaultVertexFormat.BLOCK), instance -> {
            COVER_TRANSPARENCY = instance.getUniform("CoverTransparency");
            COVER_SHADER = instance;
        });
    }

    @SubscribeEvent
    public static void blockColorsRegister(RegisterColorHandlersEvent.Block event) {
        Block[] transmitters = BuiltInRegistries.BLOCK.stream().filter(block -> block instanceof BlockTransmitter).toList().toArray(new Block[0]);
        event.register((pState, pLevel, pPos, pTintIndex) -> {
            if(pPos != null) {
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

    public static boolean isCoverTransparent() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack mainStack = player.getMainHandItem();
            ItemStack offStack = player.getOffhandItem();
            ItemStack[] stacks = new ItemStack[] { mainStack, offStack };
            boolean transparent = false;
            for (ItemStack stack : stacks) {
                if(stack.is(MekanismItems.CONFIGURATOR.get())) {
                    ItemConfigurator.ConfiguratorMode mode = MekanismItems.CONFIGURATOR.get().getMode(mainStack);
                    if(mode != ItemConfigurator.ConfiguratorMode.valueOf("COVER")) {
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

    public static float getShaderTransparency() {
        return isCoverTransparent() ? 0.333f : 1.0f;
    }

    public static void updateShaderTransparency() {
        if(COVER_TRANSPARENCY != null) {
            COVER_TRANSPARENCY.set(getShaderTransparency());
        }
    }
}
