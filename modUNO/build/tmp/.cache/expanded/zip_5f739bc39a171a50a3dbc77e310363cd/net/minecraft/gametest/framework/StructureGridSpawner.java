package net.minecraft.gametest.framework;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class StructureGridSpawner implements GameTestRunner.StructureSpawner {
    private static final int SPACE_BETWEEN_COLUMNS = 5;
    private static final int SPACE_BETWEEN_ROWS = 6;
    private final int testsPerRow;
    private int currentRowCount;
    private AABB rowBounds;
    private final BlockPos.MutableBlockPos nextTestNorthWestCorner;
    private final BlockPos firstTestNorthWestCorner;

    public StructureGridSpawner(BlockPos pFirstTestNorthWestCorner, int pTestsPerRow) {
        this.testsPerRow = pTestsPerRow;
        this.nextTestNorthWestCorner = pFirstTestNorthWestCorner.mutable();
        this.rowBounds = new AABB(this.nextTestNorthWestCorner);
        this.firstTestNorthWestCorner = pFirstTestNorthWestCorner;
    }

    @Override
    public Optional<GameTestInfo> spawnStructure(GameTestInfo pGameTestInfo) {
        BlockPos blockpos = new BlockPos(this.nextTestNorthWestCorner);
        pGameTestInfo.setNorthWestCorner(blockpos);
        pGameTestInfo.prepareTestStructure();
        AABB aabb = StructureUtils.getStructureBounds(pGameTestInfo.getStructureBlockEntity());
        this.rowBounds = this.rowBounds.minmax(aabb);
        this.nextTestNorthWestCorner.move((int)aabb.getXsize() + 5, 0, 0);
        if (++this.currentRowCount >= this.testsPerRow) {
            this.currentRowCount = 0;
            this.nextTestNorthWestCorner.move(0, 0, (int)this.rowBounds.getZsize() + 6);
            this.nextTestNorthWestCorner.setX(this.firstTestNorthWestCorner.getX());
            this.rowBounds = new AABB(this.nextTestNorthWestCorner);
        }

        return Optional.of(pGameTestInfo);
    }
}