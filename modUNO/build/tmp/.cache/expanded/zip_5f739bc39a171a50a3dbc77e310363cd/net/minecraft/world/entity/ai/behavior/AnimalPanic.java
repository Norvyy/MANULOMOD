package net.minecraft.world.entity.ai.behavior;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public class AnimalPanic<E extends PathfinderMob> extends Behavior<E> {
    private static final int PANIC_MIN_DURATION = 100;
    private static final int PANIC_MAX_DURATION = 120;
    private static final int PANIC_DISTANCE_HORIZONTAL = 5;
    private static final int PANIC_DISTANCE_VERTICAL = 4;
    private static final Predicate<PathfinderMob> DEFAULT_SHOULD_PANIC_PREDICATE = p_326813_ -> p_326813_.getLastHurtByMob() != null || p_326813_.isFreezing() || p_326813_.isOnFire();
    private final float speedMultiplier;
    private final Predicate<E> shouldPanic;

    public AnimalPanic(float pSpeedMultiplier) {
        this(pSpeedMultiplier, DEFAULT_SHOULD_PANIC_PREDICATE::test);
    }

    public AnimalPanic(float pSpeedMultiplier, Predicate<E> pShouldPanic) {
        super(Map.of(MemoryModuleType.IS_PANICKING, MemoryStatus.REGISTERED, MemoryModuleType.HURT_BY, MemoryStatus.REGISTERED), 100, 120);
        this.speedMultiplier = pSpeedMultiplier;
        this.shouldPanic = pShouldPanic;
    }

    protected boolean checkExtraStartConditions(ServerLevel pLevel, E pOwner) {
        return this.shouldPanic.test(pOwner)
            && (pOwner.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY) || pOwner.getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING));
    }

    protected boolean canStillUse(ServerLevel pLevel, E pEntity, long pGameTime) {
        return true;
    }

    protected void start(ServerLevel pLevel, E pEntity, long pGameTime) {
        pEntity.getBrain().setMemory(MemoryModuleType.IS_PANICKING, true);
        pEntity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    protected void stop(ServerLevel pLevel, E pEntity, long pGameTime) {
        Brain<?> brain = pEntity.getBrain();
        brain.eraseMemory(MemoryModuleType.IS_PANICKING);
    }

    protected void tick(ServerLevel pLevel, E pOwner, long pGameTime) {
        if (pOwner.getNavigation().isDone()) {
            Vec3 vec3 = this.getPanicPos(pOwner, pLevel);
            if (vec3 != null) {
                pOwner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3, this.speedMultiplier, 0));
            }
        }
    }

    @Nullable
    private Vec3 getPanicPos(E pPathfinder, ServerLevel pLevel) {
        if (pPathfinder.isOnFire()) {
            Optional<Vec3> optional = this.lookForWater(pLevel, pPathfinder).map(Vec3::atBottomCenterOf);
            if (optional.isPresent()) {
                return optional.get();
            }
        }

        return LandRandomPos.getPos(pPathfinder, 5, 4);
    }

    private Optional<BlockPos> lookForWater(BlockGetter pLevel, Entity pEntity) {
        BlockPos blockpos = pEntity.blockPosition();
        if (!pLevel.getBlockState(blockpos).getCollisionShape(pLevel, blockpos).isEmpty()) {
            return Optional.empty();
        } else {
            Predicate<BlockPos> predicate;
            if (Mth.ceil(pEntity.getBbWidth()) == 2) {
                predicate = p_284705_ -> BlockPos.squareOutSouthEast(p_284705_).allMatch(p_196646_ -> pLevel.getFluidState(p_196646_).is(FluidTags.WATER));
            } else {
                predicate = p_284707_ -> pLevel.getFluidState(p_284707_).is(FluidTags.WATER);
            }

            return BlockPos.findClosestMatch(blockpos, 5, 1, predicate);
        }
    }
}