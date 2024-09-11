package dev.lucaargolo.mekanismcovers;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.lucaargolo.mekanismcovers.mixed.TileEntityTransmitterMixed;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.block.transmitter.BlockTransmitter;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.util.WorldUtils;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.io.IOException;

@Mod(MekanismCovers.MODID)
public class MekanismCovers {

    public static final String MODID = "mekanismcovers";

    public static final ModelProperty<BlockState> COVER_STATE = new ModelProperty<>();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> COVER = ITEMS.register("cover", () -> new CoverItem(new Item.Properties()));
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static final RegistryObject<RecipeSerializer<CoverRecipe>> COVER_SERIALIZER = RECIPE_SERIALIZERS.register("crafting_special_cover", () -> new SimpleCraftingRecipeSerializer<>(CoverRecipe::new));

    public static final ResourceLocation COVER_MODEL = new ResourceLocation(MODID, "block/cover");

    public MekanismCovers() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(COVER);
    }

    public static void removeCover(Level world, BlockEntity tile, BlockState state, BlockPos pos, TileEntityTransmitterMixed transmitter) {
        BlockState coverState = transmitter.mekanism_covers$getCoverState();
        ItemStack currentStack = new ItemStack(MekanismCovers.COVER.get());
        currentStack.getOrCreateTag().putString("CoverState", BlockStateParser.serialize(coverState));
        Containers.dropItemStack(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, currentStack);
        transmitter.mekanism_covers$setCoverState(null);
        tile.setChanged();
        world.sendBlockUpdated(pos, state, state, 3);
        world.getLightEngine().checkBlock(pos);
    }

}
