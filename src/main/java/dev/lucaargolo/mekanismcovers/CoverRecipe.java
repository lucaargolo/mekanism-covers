package dev.lucaargolo.mekanismcovers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CoverRecipe extends CustomRecipe {

    private static final Ingredient COVER_INGREDIENT = Ingredient.of(MekanismCovers.COVER.get(), MekanismCovers.EMPTY_COVER.get());

    public CoverRecipe(CraftingBookCategory pCategory) {
        super(pCategory);
    }

    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        int coverQnt = 0;
        boolean coverFull = false;
        int blockQnt = 0;

        for(int slot = 0; slot < input.size(); ++slot) {
            ItemStack stack = input.getItem(slot);
            if (!stack.isEmpty()) {
                if (COVER_INGREDIENT.test(stack)) {
                    coverQnt++;
                    coverFull = coverFull || stack.has(MekanismCovers.COVER_BLOCK);
                }else if(stack.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof EntityBlock)) {
                    Block block = blockItem.getBlock();
                    BlockState state = block.defaultBlockState();
                    if(state.isCollisionShapeFullBlock(level, BlockPos.ZERO)) {
                        blockQnt++;
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }
        }

        return coverQnt == 1 && ((!coverFull && blockQnt == 1) || (coverFull && blockQnt == 0));
    }

    public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider registries) {
        Block block = Blocks.AIR;
        boolean isCleanOperation = false;
        for(int slot = 0; slot < input.size(); ++slot) {
            ItemStack stack = input.getItem(slot);
            if (!stack.isEmpty()) {
                if (COVER_INGREDIENT.test(stack)) {
                    isCleanOperation = isCleanOperation || stack.has(MekanismCovers.COVER_BLOCK);
                }else if(stack.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof EntityBlock)) {
                    block = blockItem.getBlock();
                }
            }
        }
        if (isCleanOperation) {
            return new ItemStack(MekanismCovers.EMPTY_COVER.get());
        }else if(block != Blocks.AIR) {
            ItemStack result = new ItemStack(MekanismCovers.COVER.get());
            result.set(MekanismCovers.COVER_BLOCK, MekanismCovers.getKey(block));
            return result;
        }
        //Technically it should never reach this.
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput input) {
        NonNullList<ItemStack> list = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for(int i = 0; i < list.size(); ++i) {
            ItemStack stack = input.getItem(i);
            Block coverBlock = MekanismCovers.getBlock(stack.get(MekanismCovers.COVER_BLOCK));
            if (coverBlock != null) {
                list.set(i, coverBlock.asItem().getDefaultInstance());
            }
        }

        return list;
    }


    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }


    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return new ItemStack(MekanismCovers.COVER);
    }

    public @NotNull RecipeSerializer<?> getSerializer() {
        return MekanismCovers.COVER_SERIALIZER.get();
    }


}
