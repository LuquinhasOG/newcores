package net.lucasoligar.newcores.block.custom;

import com.mojang.serialization.MapCodec;
import net.lucasoligar.newcores.block.entity.custom.ForgeAnvilBlockEntity;
import net.lucasoligar.newcores.item.ModItems;
import net.lucasoligar.newcores.item.custom.HammerItem;
import net.lucasoligar.newcores.recipe.ForgingRecipe;
import net.lucasoligar.newcores.recipe.ForgingRecipeInput;
import net.lucasoligar.newcores.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ForgeAnvilBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE_EAST = Block.box(3, 0, 0, 13, 16, 16);
    public static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 3, 16, 16, 13);
    public static final MapCodec<ForgeAnvilBlock> CODEC = simpleCodec(ForgeAnvilBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ForgeAnvilBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction dir = pState.getValue(FACING);
        return switch (dir) {
            case EAST, WEST -> SHAPE_EAST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ForgeAnvilBlockEntity(pPos, pState);
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            if (pLevel.getBlockEntity(pPos) instanceof ForgeAnvilBlockEntity forgeAnvilBlockEntity) {
                forgeAnvilBlockEntity.dropItems();
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    private Optional<RecipeHolder<ForgingRecipe>> currentRecipe(Level level, BlockPos pos) {
        ForgeAnvilBlockEntity block = (ForgeAnvilBlockEntity) level.getBlockEntity(pos);

        return level.getRecipeManager().getRecipeFor(ModRecipes.FORGING_TYPE.get(),
                new ForgingRecipeInput(block.inv.getStackInSlot(0)), level);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos,
                                              Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (pLevel.getBlockEntity(pPos) instanceof ForgeAnvilBlockEntity forgeAnvil) {
            ItemStack onHand = pPlayer.getItemInHand(InteractionHand.MAIN_HAND);

            if (forgeAnvil.inv.getStackInSlot(0).isEmpty() && !onHand.isEmpty()) {
                if (onHand.is(ModItems.MITHRIL_INGOT.get()) || onHand.is(ModItems.SILVER_INGOT.get())) {
                    forgeAnvil.inv.insertItem(0, onHand.copy(), false);
                    onHand.shrink(1);
                    pLevel.playSound(pPlayer, pPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
                    return ItemInteractionResult.SUCCESS;
                }
            } else if (!forgeAnvil.inv.getStackInSlot(0).isEmpty()) {
                ItemStack onAnvil = forgeAnvil.inv.extractItem(0, 1, false);
                pPlayer.addItem(onAnvil);
                forgeAnvil.clearContents();
                pLevel.playSound(pPlayer, pPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void attack(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        if (pLevel.getBlockEntity(pPos) instanceof ForgeAnvilBlockEntity forgeAnvil) {
            ItemStack onHand = pPlayer.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStackHandler inv = forgeAnvil.inv;

            if (onHand.getItem() instanceof HammerItem) {
                Optional<RecipeHolder<ForgingRecipe>> recipe = currentRecipe(pLevel, pPos);

                if (recipe.isPresent()) {
                    ItemStack output = recipe.get().value().output();
                    inv.extractItem(0, 1, false);
                    inv.insertItem(0, output.copy(), false);

                    onHand.hurtAndBreak(1, pPlayer, EquipmentSlot.MAINHAND);
                    pLevel.playSound(null, pPos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1f, 1f);
                }
            }
        }

        super.attack(pState, pLevel, pPos, pPlayer);
    }
}
