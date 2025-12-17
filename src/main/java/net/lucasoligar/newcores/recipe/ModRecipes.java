package net.lucasoligar.newcores.recipe;

import net.lucasoligar.newcores.NewCoresMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, NewCoresMod.MODID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES,  NewCoresMod.MODID);

    public static final RegistryObject<RecipeSerializer<ForgingRecipe>> FORGING_SERIALIZER
            = SERIALIZERS.register("forging", ForgingRecipe.Serializer::new);


    public static final RegistryObject<RecipeType<ForgingRecipe>> FORGING_TYPE
            = TYPES.register("forging", () -> new RecipeType<ForgingRecipe>() {
        @Override
        public String toString() {
            return "forging";
        }
    });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
