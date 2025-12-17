package net.lucasoligar.newcores.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class LightningSummonerItem extends Item {
    public LightningSummonerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();

        LightningBolt raio = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
        raio.moveTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        level.addFreshEntity(raio);

        level.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 1f, 1f);

        pContext.getItemInHand().shrink(1);

        return InteractionResult.SUCCESS;
    }
}
