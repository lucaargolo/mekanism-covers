package dev.lucaargolo.mekanismcovers;

import dev.lucaargolo.mekanismcovers.mixed.TileEntityTransmitterMixed;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(MekanismCovers.MODID)
public class MekanismCovers {

    /*
    * TODO:
    *  - Fix Nvidium compatibility.
    * */

    public static final String MODID = "mekanismcovers";

    public static final ModelProperty<BlockState> COVER_STATE = new ModelProperty<>();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> EMPTY_COVER = ITEMS.register("empty_cover", () -> new EmptyCoverItem(new Item.Properties()));
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
        ItemStack blockItemStack = coverState.getBlock().asItem().getDefaultInstance();
        ItemStack currentStack = new ItemStack(MekanismCovers.COVER.get());
        currentStack.getOrCreateTag().put("CoverBlockItem", blockItemStack.save(new CompoundTag()));
        Containers.dropItemStack(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, currentStack);
        transmitter.mekanism_covers$setCoverState(null);
        tile.setChanged();
        world.sendBlockUpdated(pos, state, state, 3);
        world.getLightEngine().checkBlock(pos);
        PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)).send(new ClientboundLightUpdatePacket(new ChunkPos(pos), world.getLightEngine(), null, null));
    }

}
