package com.hollingsworth.arsnouveau.common.entity.statemachine.alakarkinos;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ConvertBlockState extends CrabState {
    BlockPos target;
    int waitTicks;
    boolean didBubbles;

    boolean spawnedFlyingItem;

    public ConvertBlockState(Alakarkinos alakarkinos, BlockPos target) {
        super(alakarkinos);
        this.target = target;
    }

    @Override
    public void onEnd() {
        super.onEnd();
        alakarkinos.lookAt = null;
    }

    @Override
    public @Nullable CrabState tick() {

        if (waitTicks > 0) {
            waitTicks--;
            return null;
        }

        if (BlockUtil.distanceFrom(alakarkinos.blockPosition(), target) > 3) {
            alakarkinos.getNavigation().moveTo(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 1.0);
            return null;
        }
        if (!didBubbles) {
            alakarkinos.getNavigation().stop();
            alakarkinos.lookAt = Vec3.atCenterOf(target);
            didBubbles = true;
            waitTicks = 60;
            alakarkinos.setBlowingBubbles(true);
            alakarkinos.getEntityData().set(Alakarkinos.BLOWING_AT, Optional.of(target));
            return null;
        }
        var hatPos = alakarkinos.hatPos;
        if(hatPos == null){
            return new DecideCrabActionState(alakarkinos);
        }
        if (!spawnedFlyingItem) {
            spawnedFlyingItem = true;
            alakarkinos.setBlowingBubbles(false);
            EntityFlyingItem flyingItem = new EntityFlyingItem(alakarkinos.level, target, hatPos.above());
            flyingItem.getEntityData().set(EntityFlyingItem.IS_BUBBLE, true);
            alakarkinos.level.addFreshEntity(flyingItem);
            var wasGravel = alakarkinos.level.getBlockState(target).is(BlockTagProvider.ALAKARKINOS_GRAVEL);
            flyingItem.setStack(alakarkinos.level.getBlockState(target).getBlock().asItem().getDefaultInstance());
            alakarkinos.level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
//            alakarkinos.level.setBlock(target, wasGravel ? Blocks.SUSPICIOUS_GRAVEL.defaultBlockState() : Blocks.SUSPICIOUS_SAND.defaultBlockState(), 3);
//            if(alakarkinos.level.getBlockEntity(target) instanceof BrushableBlockEntityAccessor brushableBlock){
//                brushableBlock.setLootTable(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY);
//                brushableBlock.setLootTableSeed(alakarkinos.getRandom().nextLong());
//            }
            waitTicks = 60;
            return null;
        }
        return new SpawnLootState(alakarkinos);
    }
}
