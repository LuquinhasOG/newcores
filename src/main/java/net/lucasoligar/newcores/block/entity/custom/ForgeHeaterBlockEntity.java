package net.lucasoligar.newcores.block.entity.custom;

import com.mojang.logging.LogUtils;
import net.lucasoligar.newcores.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ForgeHeaterBlockEntity extends BlockEntity {
    public final ItemStackHandler inv = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public static int NONE = 0;
    public static int WARM = 1;
    public static int HOT = 2;
    public static int SCALDING = 3;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private boolean lit = false;
    private int fuel = 0;
    private int temperatureLevel = NONE;

    public ForgeHeaterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FORGE_HEATER_BE.get(), pPos, pBlockState);

        data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> fuel;
                    case 1 -> temperatureLevel;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0: ForgeHeaterBlockEntity.this.fuel = pValue;
                    case 1: ForgeHeaterBlockEntity.this.temperatureLevel = pValue;
                };
            }

            @Override
            public int getCount() {
                return 1;
            }
        };
    }

    public boolean isSlotFree(int slot) {
        return inv.getStackInSlot(slot).isEmpty();
    }

    public boolean isLit() {
        return lit;
    }

    public void dropItems() {
        SimpleContainer aux = new SimpleContainer(inv.getSlots());
        for (int i = 0; i  < inv.getSlots(); i++)
            aux.setItem(i, inv.getStackInSlot(i));

        Containers.dropContents(this.level, this.worldPosition, aux);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putInt("fuel", fuel);
        pTag.putInt("temperature", temperatureLevel);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        fuel = pTag.getInt("fuel");
        temperatureLevel = pTag.getInt("temperature");
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> inv);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        Logger log = LogUtils.getLogger();
        ItemStack fuelItem = inv.getStackInSlot(0);
        setChanged(level, blockPos, blockState);

        if (level.isClientSide()) { return; }

        if (isLit()) { fuel--; }

        if (isLit() != (fuel > 0)) {
            lit = !lit;

            BlockState currentState = level.getBlockState(blockPos);
            BlockState newState = currentState.setValue(BlockStateProperties.LIT, isLit());

            level.setBlock(blockPos, newState, 3);
            temperatureLevel = NONE;
        }

        if (!fuelItem.isEmpty()) {
            int burnTime = ForgeHooks.getBurnTime(fuelItem, RecipeType.SMELTING);
            if (burnTime > 4000) {
                temperatureLevel = SCALDING;
            } else if (burnTime > 1600 && temperatureLevel <= HOT) {
                temperatureLevel = HOT;
            } else if (temperatureLevel < HOT) {
                temperatureLevel = WARM;
            }

            fuel += 800;

            inv.extractItem(0, 1, false);
            if (fuelItem.getItem() instanceof BucketItem) {
                inv.insertItem(0, new ItemStack(Items.BUCKET), false);
                dropItems();
            }
        }
    }
}
