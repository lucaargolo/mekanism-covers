package dev.lucaargolo.mekanismcovers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EmptyCoverItem extends Item {

    public EmptyCoverItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("text.mekanismcovers.empty").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    @Override
    public @NotNull String getDescriptionId() {
        return MekanismCovers.COVER.get().getDescriptionId();
    }
}
