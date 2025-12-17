package net.lucasoligar.newcores.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record ForgingRecipeInput(ItemStack input) implements RecipeInput {

    @Override
    public ItemStack getItem(int pIndex) {
        return input;
    }

    @Override
    public int size() {
        return 1;
    }
}
