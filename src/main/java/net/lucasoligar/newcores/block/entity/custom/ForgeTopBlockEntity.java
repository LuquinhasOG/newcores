package net.lucasoligar.newcores.block.entity.custom;

import com.mojang.logging.LogUtils;
import net.lucasoligar.newcores.block.entity.ModBlockEntities;
import net.lucasoligar.newcores.recipe.HeatingRecipe;
import net.lucasoligar.newcores.recipe.HeatingRecipeInput;
import net.lucasoligar.newcores.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;

public class ForgeTopBlockEntity extends BlockEntity {
    public final ItemStackHandler inv = new ItemStackHandler(2) {
        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private int progress = 0;
    private int maxProgress = 100;

    public ForgeTopBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FORGE_TOP_BE.get(), pPos, pBlockState);
    }

    public void clearContents() {
        for (int i = 0; i  < inv.getSlots(); i++)
            inv.setStackInSlot(i, ItemStack.EMPTY);
    }

    public void dropItems() {
        SimpleContainer aux = new SimpleContainer(inv.getSlots());
        for (int i = 0; i  < inv.getSlots(); i++)
            aux.setItem(i, inv.getStackInSlot(i));

        Containers.dropContents(this.level, this.worldPosition, aux);
    }

    public boolean isSlotFree(int slot) {
        return inv.getStackInSlot(slot).isEmpty();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("inventory", inv.serializeNBT(pRegistries));
        pTag.putInt("progress", progress);
        pTag.putInt("max_progress", maxProgress);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        inv.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("progress");
        maxProgress = pTag.getInt("max_progress");
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

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net,
                             ClientboundBlockEntityDataPacket pkt,
                             HolderLookup.Provider registries) {
        loadAdditional(pkt.getTag(), registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }


    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (level.isClientSide()) return;

        BlockPos below = blockPos.below();
        Logger log = LogUtils.getLogger();

        if (level.getBlockEntity(below) instanceof ForgeHeaterBlockEntity heater) {
            Optional<RecipeHolder<HeatingRecipe>> recipe = getRecipe(heater.getTemperatureLevel());

            if (heater.isLit() && recipe.isPresent()) {
                progress++;

                log.debug("{}, {}, {}", recipe.get().value().getIngredients(), recipe.get().value().temperatureLevel(), progress);

                if (isFinished()) {
                    craftItem(recipe.get());
                    resetProgress();
                }
            } else {
                resetProgress();
                setChanged(level, blockPos, blockState);
            }
        }

        level.sendBlockUpdated(blockPos, blockState, blockState, 3);
    }

    private void craftItem(RecipeHolder<HeatingRecipe> recipe) {
        ItemStack output = recipe.value().output();
        inv.extractItem(0, 1, false);
        inv.insertItem(1, output.copy(), false);
    }

    private Optional<RecipeHolder<HeatingRecipe>> getRecipe(int temperatureLevel) {
        HeatingRecipeInput input = new HeatingRecipeInput(inv.getStackInSlot(0), temperatureLevel);

        return this.level.getRecipeManager().getRecipeFor(ModRecipes.HEATING_TYPE.get(), input, this.level);
    }

    private void resetProgress() {
        progress = 0;
    }

    public boolean isFinished() {
        return progress >= maxProgress;
    }
}
