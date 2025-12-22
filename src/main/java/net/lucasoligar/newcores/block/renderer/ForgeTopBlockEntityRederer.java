package net.lucasoligar.newcores.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lucasoligar.newcores.block.custom.ForgeAnvilBlock;
import net.lucasoligar.newcores.block.entity.custom.ForgeAnvilBlockEntity;
import net.lucasoligar.newcores.block.entity.custom.ForgeTopBlockEntity;
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
import org.joml.Vector3d;

/**
 * Glitch visual, Block Entity não atualiza do lado do cliente para o Rederer redesenhar os items
 *
 * **/

public class ForgeTopBlockEntityRederer implements BlockEntityRenderer<ForgeTopBlockEntity> {
    private ItemStack cachedInput = ItemStack.EMPTY;
    private ItemStack cachedOutput = ItemStack.EMPTY;


    public ForgeTopBlockEntityRederer(BlockEntityRendererProvider.Context context) {
    }

    public void renderItem(ItemStack stack, ItemRenderer renderer, Vector3d pos, ForgeTopBlockEntity pBlockEntity, PoseStack pPoseStack,
                           MultiBufferSource pBufferSource) {
        if (!stack.isEmpty()) {
            pPoseStack.pushPose();
            pPoseStack.translate(pos.x, pos.y, pos.z);
            pPoseStack.scale(0.5f, 0.5f, 0.5f);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90));

            renderer.renderStatic(stack, ItemDisplayContext.FIXED, getLightLevel(pBlockEntity.getLevel(),
                            pBlockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, pPoseStack, pBufferSource,
                    pBlockEntity.getLevel(), 1);
            pPoseStack.popPose();
        }
    }

    @Override
    public void render(ForgeTopBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.getLevel() == null) return;

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        Vector3d pos = new Vector3d(0.5f, 0.07f, 0.5f);

        ItemStack input = pBlockEntity.inv.getStackInSlot(0);
        ItemStack output = pBlockEntity.inv.getStackInSlot(1);

        if (!ItemStack.matches(input, cachedInput)) {
            cachedInput = input.copy();
        }

        if (!ItemStack.matches(output, cachedOutput)) {
            cachedOutput = output.copy();
        }

        renderItem(cachedInput, renderer, pos, pBlockEntity, pPoseStack, pBufferSource);
        renderItem(cachedOutput, renderer, pos, pBlockEntity, pPoseStack, pBufferSource);
    }

    private int getLightLevel(Level level, BlockPos pos) {
        return LightTexture.pack(level.getBrightness(LightLayer.SKY, pos), level.getBrightness(LightLayer.BLOCK, pos));
    }
}
