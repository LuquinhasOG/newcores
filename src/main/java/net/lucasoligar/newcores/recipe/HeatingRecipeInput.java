package net.lucasoligar.newcores.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record HeatingRecipeInput(ItemStack ingredient, int temperatureLevel) implements RecipeInput {
    @Override
    public ItemStack getItem(int pIndex) {
        return ingredient;
    }

    @Override
    public int size() {
        return 1;
    }
}
