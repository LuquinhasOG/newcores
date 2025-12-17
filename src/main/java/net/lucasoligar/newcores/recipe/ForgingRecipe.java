package net.lucasoligar.newcores.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record ForgingRecipe(Ingredient input, ItemStack output) implements Recipe<ForgingRecipeInput> {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }

    // read recipes JSON to ForgingRecipe
    @Override
    public boolean matches(ForgingRecipeInput pInput, Level pLevel) {
        if (pLevel.isClientSide())
            return false;

        return input.test(pInput.getItem(0));
    }

    @Override
    public ItemStack assemble(ForgingRecipeInput pInput, HolderLookup.Provider pRegistries) {
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
        return ModRecipes.FORGING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.FORGING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ForgingRecipe> {
        public static final MapCodec<ForgingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(ForgingRecipe::input),
                ItemStack.CODEC.fieldOf("result").forGetter(ForgingRecipe::output)
                ).apply(inst, ForgingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ForgingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, ForgingRecipe::input, ItemStack.STREAM_CODEC,
                        ForgingRecipe::output, ForgingRecipe::new);

        @Override
        public MapCodec<ForgingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ForgingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
