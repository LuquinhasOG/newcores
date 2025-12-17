package net.lucasoligar.newcores.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lucasoligar.newcores.block.custom.ForgeAnvilBlock;
import net.lucasoligar.newcores.block.entity.custom.ForgeAnvilBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class ForgeAnvilBlockEntityRederer implements BlockEntityRenderer<ForgeAnvilBlockEntity> {
    public ForgeAnvilBlockEntityRederer(BlockEntityRendererProvider.Context context) {
    }

    public void renderItem(ItemStack stack, ItemRenderer renderer, Vector3d pos, ForgeAnvilBlockEntity pBlockEntity, PoseStack pPoseStack,
                           MultiBufferSource pBufferSource) {
        if (!stack.isEmpty()) {
            pPoseStack.pushPose();
            pPoseStack.translate(pos.x, pos.y, pos.z);
            pPoseStack.scale(0.35f, 0.35f, 0.35f);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90));

            renderer.renderStatic(stack, ItemDisplayContext.FIXED, getLightLevel(pBlockEntity.getLevel(),
                            pBlockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, pPoseStack, pBufferSource,
                    pBlockEntity.getLevel(), 1);
            pPoseStack.popPose();
        }
    }

    @Override
    public void render(ForgeAnvilBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

        ItemStack stack1 = pBlockEntity.inv.getStackInSlot(ForgeAnvilBlockEntity.MATERIAL_SLOT);
        ItemStack stack2 = pBlockEntity.inv.getStackInSlot(ForgeAnvilBlockEntity.ARMOR_TOOL_SLOT);
        ItemStack stack3 = pBlockEntity.inv.getStackInSlot(ForgeAnvilBlockEntity.EXTRA_SLOT);
        ItemStack stack4 = pBlockEntity.inv.getStackInSlot(ForgeAnvilBlockEntity.OUTPUT_SLOT);

        Vector3d leftPos = new Vector3d(0.8f, 1.01f, 0.5f);
        Vector3d rightPos = new Vector3d(0.2f, 1.01f, 0.5f);

        Direction dir = pBlockEntity.getBlockState().getValue(ForgeAnvilBlock.FACING);
        if (dir == Direction.WEST) {
            leftPos = new Vector3d(0.5f, 1.01f, 0.2f);
            rightPos = new Vector3d(0.5f, 1.01f, 0.8f);
        } else if (dir == Direction.EAST) {
            leftPos = new Vector3d(0.5f, 1.01f, 0.8f);
            rightPos = new Vector3d(0.5f, 1.01f, 0.2f);
        } else if (dir == Direction.SOUTH) {
            leftPos = new Vector3d(0.2f, 1.01f, 0.5f);
            rightPos = new Vector3d(0.8f, 1.01f, 0.5f);
        }

        renderItem(stack1, renderer, leftPos, pBlockEntity, pPoseStack, pBufferSource);
        renderItem(stack2, renderer, new Vector3d(0.5f, 1.01f, 0.5f), pBlockEntity, pPoseStack, pBufferSource);
        renderItem(stack3, renderer, rightPos, pBlockEntity, pPoseStack, pBufferSource);
        renderItem(stack4, renderer, new Vector3d(0.5f, 1.01f, 0.5f), pBlockEntity, pPoseStack, pBufferSource);
    }

    private int getLightLevel(Level level, BlockPos pos) {
        return LightTexture.pack(level.getBrightness(LightLayer.SKY, pos), level.getBrightness(LightLayer.BLOCK, pos));
    }
}
