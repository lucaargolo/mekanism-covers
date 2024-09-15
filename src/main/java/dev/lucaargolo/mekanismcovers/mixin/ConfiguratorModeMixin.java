package dev.lucaargolo.mekanismcovers.mixin;

import com.mojang.serialization.Codec;
import dev.lucaargolo.mekanismcovers.MekanismCovers;
import io.netty.buffer.ByteBuf;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.lib.transmitter.TransmissionType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.IntFunction;

@Mixin(value = ItemConfigurator.ConfiguratorMode.class, remap = false)
public class ConfiguratorModeMixin {

    @Invoker("<init>")
    public static ItemConfigurator.ConfiguratorMode invokeInit(String name, int id, ILangEntry langEntry, @Nullable TransmissionType transmissionType, EnumColor color, boolean configurating, @Nullable ResourceLocation icon) { throw new AssertionError(); }

    @Shadow @Final @Mutable
    private static ItemConfigurator.ConfiguratorMode[] $VALUES;

    @Shadow @Final @Mutable
    public static Codec<ItemConfigurator.ConfiguratorMode> CODEC;

    @Shadow @Final @Mutable
    public static final IntFunction<ItemConfigurator.ConfiguratorMode> BY_ID;

    @Shadow @Final @Mutable
    public static final StreamCodec<ByteBuf, ItemConfigurator.ConfiguratorMode> STREAM_CODEC;

    static {
        ArrayList<ItemConfigurator.ConfiguratorMode> values =  new ArrayList<>(Arrays.asList($VALUES));
        ItemConfigurator.ConfiguratorMode last = values.getLast();

        // add new value
        ItemConfigurator.ConfiguratorMode newValue = invokeInit("COVER", last.ordinal() + 1, () -> "item.mekanismcovers.cover", null, EnumColor.WHITE, false, ResourceLocation.fromNamespaceAndPath(MekanismCovers.MODID, "textures/gui/cover.png"));
        values.add(newValue);

        $VALUES = values.toArray(new ItemConfigurator.ConfiguratorMode[0]);
        CODEC = StringRepresentable.fromEnum(() -> $VALUES);
        BY_ID = ByIdMap.continuous(Enum::ordinal, $VALUES, ByIdMap.OutOfBoundsStrategy.WRAP);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);
    }

}
