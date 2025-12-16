package net.lucasoligar.newcores.item;

import net.lucasoligar.newcores.NewCoresMod;
import net.lucasoligar.newcores.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NewCoresMod.MODID);

    public static final RegistryObject<CreativeModeTab> NEWCORES_TAB = CREATIVE_TAB.register("newcores_creative_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.MITHRIL_INGOT.get()))
                    .title(Component.translatable("creativetab.newcores.newcores_creative_tab"))
                    .displayItems((display, output) -> {
                        output.accept(ModBlocks.SILVER_ORE.get());
                        output.accept(ModBlocks.DEEPSLATE_SILVER_ORE.get());
                        output.accept(ModItems.RAW_SILVER.get());
                        output.accept(ModItems.SILVER_INGOT.get());
                        output.accept(ModItems.SILVER_PLATE.get());

                        output.accept(ModBlocks.DEEPSLATE_MITHRIL_ORE.get());
                        output.accept(ModItems.RAW_MITHRIL.get());
                        output.accept(ModItems.MITHRIL_INGOT.get());
                        output.accept(ModItems.MITHRIL_PLATE.get());

                        output.accept(ModBlocks.FORGE_ANVIL.get());
                        output.accept(ModItems.IRON_HAMMER.get());

                        output.accept(ModBlocks.BLACK_CORE.get());
                        output.accept(ModBlocks.WHITE_CORE.get());
                    })
                    .build());

        public static void register(IEventBus eventBus) {
        CREATIVE_TAB.register(eventBus);
    }
}
