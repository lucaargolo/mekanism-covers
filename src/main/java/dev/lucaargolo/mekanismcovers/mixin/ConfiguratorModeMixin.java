package dev.lucaargolo.mekanismcovers.mixin;

import dev.lucaargolo.mekanismcovers.MekanismCovers;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.lib.transmitter.TransmissionType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(value = ItemConfigurator.ConfiguratorMode.class, remap = false)
public class ConfiguratorModeMixin {

    @Invoker("<init>")
    public static ItemConfigurator.ConfiguratorMode invokeInit(String name, int id, ILangEntry langEntry, @Nullable TransmissionType transmissionType, EnumColor color, boolean configurating, @Nullable ResourceLocation icon) { throw new AssertionError(); }

    @Shadow @Final @Mutable
    private static ItemConfigurator.ConfiguratorMode[] $VALUES;

    @Shadow @Final @Mutable
    private static ItemConfigurator.ConfiguratorMode[] MODES;

    static {
        ArrayList<ItemConfigurator.ConfiguratorMode> values =  new ArrayList<>(Arrays.asList($VALUES));
        ItemConfigurator.ConfiguratorMode last = values.get(values.size() - 1);

        // add new value
        ItemConfigurator.ConfiguratorMode newValue = invokeInit("COVER", last.ordinal() + 1, () -> "item.mekanismcovers.cover", null, EnumColor.WHITE, false, new ResourceLocation(MekanismCovers.MODID, "textures/gui/cover.png"));
        System.out.println(newValue);
        values.add(newValue);

        $VALUES = values.toArray(new ItemConfigurator.ConfiguratorMode[0]);
        MODES = $VALUES;
    }

}
