package net.lucasoligar.newcores.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * ForgingRecipe
 *
 * Type name: newcores:heating
 * Ingredient Fields: ingredient
 * Output Field: result
 *
 * JSON Example:
 * {
 *   "type": "newcores:forging",
 *   "ingredient": { "item": "newcores:silver_ingot" },
 *   "result": { "id": "newcores:heated_silver_ingot" }
 * }
 * **/

public record HeatingRecipe(Ingredient ingredient, int temperatureLevel, ItemStack output)
        implements Recipe<HeatingRecipeInput> {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);

        return list;
    }

    // read recipes JSON to ForgingRecipe
    @Override
    public boolean matches(HeatingRecipeInput pInput, Level pLevel) {
        return ingredient.test(pInput.getItem(0)) && (pInput.temperatureLevel() >= temperatureLevel);
    }

    @Override
    public ItemStack assemble(HeatingRecipeInput pInput, HolderLookup.Provider pRegistries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.HEATING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.HEATING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<HeatingRecipe> {
        public static final MapCodec<HeatingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(HeatingRecipe::ingredient),
                Codec.INT.fieldOf("temp_level").forGetter(HeatingRecipe::temperatureLevel),
                ItemStack.CODEC.fieldOf("result").forGetter(HeatingRecipe::output)
                ).apply(inst, HeatingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HeatingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, HeatingRecipe::ingredient,
                        ByteBufCodecs.INT, HeatingRecipe::temperatureLevel,
                        ItemStack.STREAM_CODEC, HeatingRecipe::output, HeatingRecipe::new);

        @Override
        public MapCodec<HeatingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HeatingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
