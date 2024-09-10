package dev.lucaargolo.mekanismcovers;

import mekanism.common.block.states.BlockStateHelper;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CoverRecipe extends CustomRecipe {

    private static final Ingredient COVER_INGREDIENT = Ingredient.of(MekanismCovers.COVER.get());

    public CoverRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    @SuppressWarnings("deprecation")
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        int coverQnt = 0;
        boolean coverFull = false;
        int blockQnt = 0;
        for(int slot = 0; slot < pInv.getContainerSize(); ++slot) {
            ItemStack stack = pInv.getItem(slot);
            if (!stack.isEmpty()) {
                if (COVER_INGREDIENT.test(stack)) {
                    coverQnt++;
                    coverFull = coverFull || (stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("CoverState"));
                }else if(stack.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof EntityBlock)) {
                    Block block = blockItem.getBlock();
                    BlockState state = block.defaultBlockState();
                    if(block.isCollisionShapeFullBlock(state, pLevel, BlockPos.ZERO)) {
                        blockQnt++;
                    }
                }else{
                    return false;
                }
            }
        }

        return coverQnt == 1 && ((!coverFull && blockQnt == 1) || (coverFull && blockQnt == 0));
    }

    public @NotNull ItemStack assemble(CraftingContainer pInv, @NotNull RegistryAccess pRegistryAccess) {
        Block block = Blocks.AIR;
        boolean isCleanOperation = false;
        for(int slot = 0; slot < pInv.getContainerSize(); ++slot) {
            ItemStack stack = pInv.getItem(slot);
            if (!stack.isEmpty()) {
                if (COVER_INGREDIENT.test(stack)) {
                    isCleanOperation = isCleanOperation || (stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("CoverState"));
                }else if(stack.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof EntityBlock)) {
                    block = blockItem.getBlock();
                }
            }
        }
        ItemStack result = new ItemStack(MekanismCovers.COVER.get());
        if (isCleanOperation) {
            return result;
        }else if(block != Blocks.AIR) {
            String serialized = BlockStateParser.serialize(block.defaultBlockState());
            result.getOrCreateTag().putString("CoverState", serialized);
            return result;
        }
        //Technically it should never reach this.
        return ItemStack.EMPTY;
    }

    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return new ItemStack(MekanismCovers.COVER.get());
    }

    public RecipeSerializer<?> getSerializer() {
        return MekanismCovers.COVER_SERIALIZER.get();
    }


}
