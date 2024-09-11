package dev.lucaargolo.mekanismcovers;

import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CoverRecipe extends CustomRecipe {

    private static final Ingredient COVER_INGREDIENT = Ingredient.of(MekanismCovers.COVER.get(), MekanismCovers.EMPTY_COVER.get());

    public CoverRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    @SuppressWarnings("deprecation")
    public boolean matches(CraftingContainer pInv, @NotNull Level pLevel) {
        int coverQnt = 0;
        boolean coverFull = false;
        int blockQnt = 0;
        for(int slot = 0; slot < pInv.getContainerSize(); ++slot) {
            ItemStack stack = pInv.getItem(slot);
            if (!stack.isEmpty()) {
                if (COVER_INGREDIENT.test(stack)) {
                    coverQnt++;
                    coverFull = coverFull || (stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("CoverBlockItem"));
                }else if(stack.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof EntityBlock)) {
                    Block block = blockItem.getBlock();
                    BlockState state = block.defaultBlockState();
                    if(block.isCollisionShapeFullBlock(state, pLevel, BlockPos.ZERO)) {
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

    public @NotNull ItemStack assemble(CraftingContainer pInv, @NotNull RegistryAccess pRegistryAccess) {
        Block block = Blocks.AIR;
        boolean isCleanOperation = false;
        for(int slot = 0; slot < pInv.getContainerSize(); ++slot) {
            ItemStack stack = pInv.getItem(slot);
            if (!stack.isEmpty()) {
                if (COVER_INGREDIENT.test(stack)) {
                    isCleanOperation = isCleanOperation || (stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("CoverBlockItem"));
                }else if(stack.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof EntityBlock)) {
                    block = blockItem.getBlock();
                }
            }
        }
        if (isCleanOperation) {
            return new ItemStack(MekanismCovers.EMPTY_COVER.get());
        }else if(block != Blocks.AIR) {
            ItemStack result = new ItemStack(MekanismCovers.COVER.get());
            result.getOrCreateTag().put("CoverBlockItem", block.asItem().getDefaultInstance().save(new CompoundTag()));
            return result;
        }
        //Technically it should never reach this.
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingContainer pContainer) {
        NonNullList<ItemStack> list = NonNullList.withSize(pContainer.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < list.size(); ++i) {
            ItemStack stack = pContainer.getItem(i);
            if (stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("CoverBlockItem")) {
                ItemStack remaining = ItemStack.of(Objects.requireNonNull(stack.getTag()).getCompound("CoverBlockItem"));
                list.set(i, remaining);
            }
        }

        return list;
    }

    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }


    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess pRegistryAccess) {
        return new ItemStack(MekanismCovers.COVER.get());
    }

    public @NotNull RecipeSerializer<?> getSerializer() {
        return MekanismCovers.COVER_SERIALIZER.get();
    }


}
