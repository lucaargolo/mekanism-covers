package dev.lucaargolo.mekanismcovers;

import dev.lucaargolo.mekanismcovers.mixed.TileEntityTransmitterMixed;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Mod(MekanismCovers.MODID)
public class MekanismCovers {

    public static final String MODID = "mekanismcovers";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    public static final DeferredHolder<Item, EmptyCoverItem> EMPTY_COVER = ITEMS.register("empty_cover", () -> new EmptyCoverItem(new Item.Properties()));
    public static final DeferredHolder<Item, CoverItem> COVER = ITEMS.register("cover", () -> new CoverItem(new Item.Properties()));

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> COVER_BLOCK = DATA_COMPONENT_TYPES.register("cover_block", () -> new DataComponentType.Builder<ResourceLocation>().persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC).cacheEncoding().build());

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<CoverRecipe>> COVER_SERIALIZER = RECIPE_SERIALIZERS.register("crafting_special_cover", () -> new SimpleCraftingRecipeSerializer<>(CoverRecipe::new));

    public static final ModelProperty<BlockState> COVER_STATE = new ModelProperty<>();
    public static final ModelProperty<ModelData> COVER_DATA = new ModelProperty<>();

    public static final Map<BlockPos, BlockState> POSSIBLE_BLOCKS = new HashMap<>();

    public MekanismCovers(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        DATA_COMPONENT_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(EMPTY_COVER.get());
    }

    public static void removeCover(Level world, BlockEntity tile, BlockState state, BlockPos pos, TileEntityTransmitterMixed transmitter, boolean update) {
        BlockState coverState = transmitter.mekanism_covers$getCoverState();
        ItemStack currentStack = new ItemStack(MekanismCovers.COVER);
        currentStack.set(COVER_BLOCK, getKey(coverState.getBlock()));
        Containers.dropItemStack(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, currentStack);
        if(update) {
            transmitter.mekanism_covers$setCoverState(null);
            tile.setChanged();
            world.sendBlockUpdated(pos, state, state, 3);
            AuxiliaryLightManager lightManager = world.getAuxLightManager(pos);
            if (lightManager != null) {
                lightManager.removeLightAt(pos);
            }
            world.getLightEngine().checkBlock(pos);
            if (world instanceof ServerLevel serverLevel) {
                for (ServerPlayer player : serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false)) {
                    player.connection.send(new ClientboundLightUpdatePacket(new ChunkPos(pos), world.getLightEngine(), null, null));
                }
            }
        }
    }

    @Nullable
    public static Block getBlock(ResourceLocation key) {
        try {
            return BuiltInRegistries.BLOCK.getOrThrow(ResourceKey.create(Registries.BLOCK, key));
        }catch (IllegalStateException ignored) {
            return null;
        }
    }


    @Nullable
    public static ResourceLocation getKey(Block block) {
        try {
            return BuiltInRegistries.BLOCK.getKey(block);
        }catch (IllegalStateException ignored) {
            return null;
        }
    }
}
