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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.Block;
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

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Client {

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
    }
}
