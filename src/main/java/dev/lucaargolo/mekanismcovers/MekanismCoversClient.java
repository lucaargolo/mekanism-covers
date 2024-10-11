package dev.lucaargolo.mekanismcovers;

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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static dev.lucaargolo.mekanismcovers.MekanismCovers.COVER_MODEL;
import static dev.lucaargolo.mekanismcovers.MekanismCovers.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MekanismCoversClient {

    public static final boolean ADVANCED_COVER_RENDERING = !ModConfig.getInstance().isDisableAdvancedCoverRendering();

    public static Short COVER_ENTITY_ID = null;

    private static boolean lastTransparency = false;

    @SubscribeEvent
    public static void registerCoverModel(ModelEvent.RegisterAdditional event) {
        event.register(COVER_MODEL);
    }

    @SubscribeEvent
    public static void blockColorsRegister(RegisterColorHandlersEvent.Block event) {
        Block[] transmitters = MekanismBlocks.BLOCKS.getAllBlocks().stream().map(IBlockProvider::getBlock).filter(block -> block instanceof BlockTransmitter).toList().toArray(new Block[0]);
        event.register((pState, pLevel, pPos, pTintIndex) -> {
            if((!MekanismCoversClient.ADVANCED_COVER_RENDERING || pTintIndex == 1337) && pPos != null) {
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

    public static String modifyIrisVertex(String source) {
        if(MekanismCoversClient.COVER_ENTITY_ID != null) {
            String[] lines = source.split("\n");

            StringBuilder modifiedSource = new StringBuilder();
            modifiedSource.append(lines[0]).append("\n");
            if(!source.contains("mc_Entity")) {
                modifiedSource.append("in vec2 mc_Entity;\n");
            }
            modifiedSource.append("flat out int mekanismCoverInjectMat;\n");
            for (int i = 1; i < lines.length - 1; i++) {
                modifiedSource.append(lines[i]).append("\n");
            }
            modifiedSource.append("mekanismCoverInjectMat = int(mc_Entity.x + 0.5);\n");
            modifiedSource.append(lines[lines.length - 1]);

            return modifiedSource.toString();
        }else {
            return source;
        }
    }

    public static String modifyIrisFragment(String source) {
        if(MekanismCoversClient.COVER_ENTITY_ID != null) {
            String[] lines = source.split("\n");

            StringBuilder modifiedSource = new StringBuilder();
            modifiedSource.append(lines[0]).append("\n");
            modifiedSource.append("flat in int mekanismCoverInjectMat;\n");
            modifiedSource.append("uniform float mkcv_CoverTransparency;\n");
            for (int i = 1; i < lines.length - 1; i++) {
                modifiedSource.append(lines[i]).append("\n");
            }
            modifiedSource.append("if(mekanismCoverInjectMat == ").append(MekanismCoversClient.COVER_ENTITY_ID).append(") {\n");
            modifiedSource.append("    iris_FragData0.a *= mkcv_CoverTransparency;\n");
            modifiedSource.append("}\n");
            modifiedSource.append(lines[lines.length - 1]);

            return modifiedSource.toString();
        }else {
            return source;
        }
    }

}
