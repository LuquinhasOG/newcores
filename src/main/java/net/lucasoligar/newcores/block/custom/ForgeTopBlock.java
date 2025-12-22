package net.lucasoligar.newcores.block.custom;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import net.lucasoligar.newcores.block.entity.ModBlockEntities;
import net.lucasoligar.newcores.block.entity.custom.ForgeTopBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ForgeTopBlock extends BaseEntityBlock {
    public static final MapCodec<ForgeHeaterBlock> CODEC = simpleCodec(ForgeHeaterBlock::new);
    public static final VoxelShape SHAPE = Block.box(3, 0, 0, 13, 8, 16);

    public ForgeTopBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ForgeTopBlockEntity(pPos, pState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.FORGE_TOP_BE.get(),
                (level, blockPos, blockState, forgeTopBlockEntity) -> forgeTopBlockEntity.tick(level, blockPos, blockState));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        Logger log = LogUtils.getLogger();
        log.debug("usou item");

        if (!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof ForgeTopBlockEntity forgeTop) {
            ItemStack onHand = pPlayer.getItemInHand(InteractionHand.MAIN_HAND);

            if (!onHand.isEmpty() && forgeTop.isSlotFree(0)) {
                forgeTop.inv.insertItem(0, onHand.copy(), false);
                onHand.shrink(1);
                pLevel.playSound(pPlayer, pPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);

                forgeTop.setChanged();
                pLevel.sendBlockUpdated(pPos, pState, pState, 3);

                log.debug("colocou item");
                return ItemInteractionResult.SUCCESS;
            } else if (!forgeTop.isSlotFree(0) || !forgeTop.isSlotFree(1)) {
                ItemStack slot0 = forgeTop.inv.extractItem(0, forgeTop.inv.getStackInSlot(0).getCount(), false);
                ItemStack slot1 = forgeTop.inv.extractItem(1, forgeTop.inv.getStackInSlot(1).getCount(), false);

                if (!slot0.isEmpty()) pPlayer.addItem(slot0);
                if (!slot1.isEmpty()) pPlayer.addItem(slot1);

                forgeTop.clearContents();
                pLevel.playSound(pPlayer, pPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);

                forgeTop.setChanged();
                pLevel.sendBlockUpdated(pPos, pState, pState, Block.UPDATE_ALL_IMMEDIATE);

                log.debug("tirou itens");
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.SUCCESS;
    }
}
