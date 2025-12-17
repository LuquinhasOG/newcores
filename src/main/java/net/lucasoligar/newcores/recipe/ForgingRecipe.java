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

/**
 * ForgingRecipe
 *
 * Type name: newcores:forging
 * Ingredient Fields: material, armor_tool, extra_material
 * Output Field: result
 *
 * JSON Example:
 * {
 *   "type": "newcores:forging",
 *   "material": { "item": "newcores:silver_ingot" },
 *   "result": { "id": "newcores:silver_plate" }
 * }
 * **/

public record ForgingRecipe(Ingredient material, Ingredient armorTool,Ingredient extraMaterial,
                            ItemStack output) implements Recipe<ForgingRecipeInput> {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(material);
        list.add(armorTool);
        list.add(extraMaterial);

        return list;
    }

    // read recipes JSON to ForgingRecipe
    @Override
    public boolean matches(ForgingRecipeInput pInput, Level pLevel) {
        boolean matOk = material.test(pInput.getItem(0));
        boolean toolOk = armorTool.test(pInput.getItem(1));
        boolean extraOk = extraMaterial.test(pInput.getItem(2));

        if (pInput.getItem(0).isEmpty() && material.isEmpty())
            matOk = true;

        if (pInput.getItem(1).isEmpty() && armorTool.isEmpty())
            toolOk = true;

        if (pInput.getItem(2).isEmpty() && extraMaterial.isEmpty())
            extraOk = true;


        return matOk && toolOk && extraOk;
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
                Ingredient.CODEC.optionalFieldOf("material1", Ingredient.EMPTY).forGetter(ForgingRecipe::material),
                Ingredient.CODEC.optionalFieldOf("armor_tool", Ingredient.EMPTY).forGetter(ForgingRecipe::armorTool),
                Ingredient.CODEC.optionalFieldOf("material2", Ingredient.EMPTY).forGetter(ForgingRecipe::extraMaterial),
                ItemStack.CODEC.fieldOf("result").forGetter(ForgingRecipe::output)
                ).apply(inst, ForgingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ForgingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, ForgingRecipe::material,
                        Ingredient.CONTENTS_STREAM_CODEC, ForgingRecipe::armorTool,
                        Ingredient.CONTENTS_STREAM_CODEC, ForgingRecipe::extraMaterial,
                        ItemStack.STREAM_CODEC, ForgingRecipe::output, ForgingRecipe::new);

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
