package net.lucasoligar.newcores.block.entity;

import net.lucasoligar.newcores.NewCoresMod;
import net.lucasoligar.newcores.block.ModBlocks;
import net.lucasoligar.newcores.block.entity.custom.ForgeAnvilBlockEntity;
import net.lucasoligar.newcores.block.entity.custom.ForgeHeaterBlockEntity;
import net.lucasoligar.newcores.block.entity.custom.ForgeTopBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES
            = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NewCoresMod.MODID);

    public static final RegistryObject<BlockEntityType<ForgeAnvilBlockEntity>> FORGE_ANVIL_BE
            = BLOCK_ENTITIES.register("forge_anvil_be", () -> BlockEntityType.Builder
                    .of(ForgeAnvilBlockEntity::new, ModBlocks.FORGE_ANVIL.get()).build(null));

    public static final RegistryObject<BlockEntityType<ForgeHeaterBlockEntity>> FORGE_HEATER_BE
            = BLOCK_ENTITIES.register("forge_heater_be", () -> BlockEntityType.Builder
                    .of(ForgeHeaterBlockEntity::new, ModBlocks.FORGE_HEATER.get()).build(null));

    public static final RegistryObject<BlockEntityType<ForgeTopBlockEntity>> FORGE_TOP_BE
            = BLOCK_ENTITIES.register("forge_top_be", () -> BlockEntityType.Builder
                    .of(ForgeTopBlockEntity::new, ModBlocks.FORGE_TOP.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
