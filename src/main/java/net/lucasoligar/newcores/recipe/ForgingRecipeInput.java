package net.lucasoligar.newcores.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record ForgingRecipeInput(ItemStack material, ItemStack armor_tool, ItemStack extras) implements RecipeInput {
    @Override
    public ItemStack getItem(int pIndex) {
        return switch (pIndex) {
            case 0 -> material;
            case 1 -> armor_tool;
            case 2 -> extras;
            default -> throw new IllegalStateException("Unexpected value: " + pIndex);
        };
    }

    @Override
    public int size() {
        return 3;
    }
}
