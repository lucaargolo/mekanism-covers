package dev.lucaargolo.mekanismcovers.jade;

import dev.lucaargolo.mekanismcovers.MekanismCovers;
import dev.lucaargolo.mekanismcovers.mixed.TileEntityTransmitterMixed;
import mekanism.common.block.transmitter.BlockTransmitter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.TextElement;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new CoverComponentProvider(), BlockTransmitter.class);
    }

    public static class CoverComponentProvider implements IBlockComponentProvider {

        private static final ResourceLocation COVER_COMPONENT = ResourceLocation.fromNamespaceAndPath(MekanismCovers.MODID, "cover_component");

        @Override
        public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
            if(blockAccessor.getBlockEntity() instanceof TileEntityTransmitterMixed tile) {
                BlockState state = tile.mekanism_covers$getCoverState();
                if(state != null) {
                    ItemStack stack = state.getBlock().asItem().getDefaultInstance();
                    IElementHelper elements = IElementHelper.get();
                    iTooltip.add(elements.item(stack).message(null).translate(new Vec2(-21, 0)).size(new Vec2(18, 9)));
                    TextElement text = new TextElement(state.getBlock().getName().withColor(0xFFFFFF));
                    iTooltip.append(text.translate(new Vec2(-18, 0)).size(new Vec2(text.getSize().x, 12)));
                }
            }
        }

        @Override
        public ResourceLocation getUid() {
            return COVER_COMPONENT;
        }

        @Override
        public int getDefaultPriority() {
            return -100000;
        }
    }

}
