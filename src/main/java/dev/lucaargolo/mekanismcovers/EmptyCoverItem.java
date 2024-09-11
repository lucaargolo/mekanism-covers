package dev.lucaargolo.mekanismcovers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmptyCoverItem extends Item {

    public EmptyCoverItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("text.mekanismcovers.empty").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    @Override
    public @NotNull String getDescriptionId() {
        return MekanismCovers.COVER.get().getDescriptionId();
    }
}
