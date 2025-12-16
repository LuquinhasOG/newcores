package net.lucasoligar.newcores.block;


import net.lucasoligar.newcores.NewCoresMod;
import net.lucasoligar.newcores.block.custom.ForgeAnvilBlock;
import net.lucasoligar.newcores.item.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, NewCoresMod.MODID);

    public static final RegistryObject<Block> SILVER_ORE = registerBlock("silver_ore",
            () -> new DropExperienceBlock(UniformInt.of(0, 2), BlockBehaviour.Properties.of()
                    .strength(3f, 3f).sound(SoundType.STONE).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> DEEPSLATE_SILVER_ORE = registerBlock("deepslate_silver_ore",
            () -> new DropExperienceBlock(UniformInt.of(0, 2), BlockBehaviour.Properties.of()
                    .strength(4.5F, 4.5F).sound(SoundType.STONE).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> DEEPSLATE_MITHRIL_ORE = registerBlock("deepslate_mithril_ore",
            () -> new DropExperienceBlock(UniformInt.of(0, 2), BlockBehaviour.Properties.of()
                    .strength(4.5f, 4.5f).sound(SoundType.STONE).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> FORGE_ANVIL = registerBlock("forge_anvil",
            () -> new ForgeAnvilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ANVIL)
                    .strength(5.0F, 1200.0F).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> BLACK_CORE = registerBlock("black_core",
            () -> new HeavyCoreBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.HEAVY_CORE)));

    public static final RegistryObject<Block> WHITE_CORE = registerBlock("white_core",
            () -> new HeavyCoreBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.HEAVY_CORE)));

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> blockRegistry = BLOCKS.register(name, block);
        registerBlockItem(name, blockRegistry);
        return blockRegistry;
    }

    public static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
