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
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

import static dev.lucaargolo.mekanismcovers.MekanismCovers.COVER_MODEL;
import static dev.lucaargolo.mekanismcovers.MekanismCovers.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MekanismCoversClient {

    public static ShaderInstance COVER_SHADER;
    public static Uniform COVER_TRANSPARENCY;

    @SubscribeEvent
    public static void registerCoverModel(ModelEvent.RegisterAdditional event) {
        event.register(COVER_MODEL);
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(MODID, "rendertype_cover"), DefaultVertexFormat.BLOCK), instance -> {
            COVER_TRANSPARENCY = instance.getUniform("CoverTransparency");
            COVER_SHADER = instance;
        });
    }

    @SubscribeEvent
    public static void blockColorsRegister(RegisterColorHandlersEvent.Block event) {
        Block[] transmitters = MekanismBlocks.BLOCKS.getAllBlocks().stream().map(IBlockProvider::getBlock).filter(block -> block instanceof BlockTransmitter).toList().toArray(new Block[0]);
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

    public static float getTransparency(LocalPlayer player) {
        if (player != null) {
            ItemStack mainStack = player.getMainHandItem();
            ItemStack offStack = player.getOffhandItem();
            if (mainStack.is(MekanismItems.CONFIGURATOR.get())) {
                return MekanismItems.CONFIGURATOR.get().getMode(mainStack) == ItemConfigurator.ConfiguratorMode.valueOf("COVER") ? 1.0f : 0.25f;
            } else if (offStack.is(MekanismItems.CONFIGURATOR.get())) {
                return MekanismItems.CONFIGURATOR.get().getMode(offStack) == ItemConfigurator.ConfiguratorMode.valueOf("COVER") ? 1.0f : 0.25f;
            } else {
                return 1.0f;
            }
        } else {
            return 1.0f;
        }
    }

    public static void updateTransparency(LocalPlayer player) {
        if(COVER_TRANSPARENCY != null) {
            COVER_TRANSPARENCY.set(getTransparency(player));
        }
    }
}
