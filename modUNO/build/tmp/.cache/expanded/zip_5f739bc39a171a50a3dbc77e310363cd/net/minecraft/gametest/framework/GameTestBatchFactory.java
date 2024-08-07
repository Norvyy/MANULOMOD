package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;

public class GameTestBatchFactory {
    private static final int MAX_TESTS_PER_BATCH = 50;

    public static Collection<GameTestBatch> fromTestFunction(Collection<TestFunction> pTestFunctions, ServerLevel pLevel) {
        Map<String, List<TestFunction>> map = pTestFunctions.stream().collect(Collectors.groupingBy(TestFunction::batchName));
        return map.entrySet()
            .stream()
            .flatMap(
                p_332128_ -> {
                    String s = p_332128_.getKey();
                    List<TestFunction> list = p_332128_.getValue();
                    return Streams.mapWithIndex(
                        Lists.partition(list, 50).stream(),
                        (p_328225_, p_330032_) -> toGameTestBatch(p_328225_.stream().map(p_334925_ -> toGameTestInfo(p_334925_, 0, pLevel)).toList(), s, p_330032_)
                    );
                }
            )
            .toList();
    }

    public static GameTestInfo toGameTestInfo(TestFunction pTestFunction, int pRotationSteps, ServerLevel pLevel) {
        return new GameTestInfo(pTestFunction, StructureUtils.getRotationForRotationSteps(pRotationSteps), pLevel, RetryOptions.noRetries());
    }

    public static GameTestRunner.GameTestBatcher fromGameTestInfo() {
        return p_333178_ -> {
            Map<String, List<GameTestInfo>> map = p_333178_.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(p_330180_ -> p_330180_.getTestFunction().batchName()));
            return map.entrySet().stream().flatMap(p_330166_ -> {
                String s = p_330166_.getKey();
                List<GameTestInfo> list = p_330166_.getValue();
                return Streams.mapWithIndex(Lists.partition(list, 50).stream(), (p_330098_, p_329231_) -> toGameTestBatch(List.copyOf(p_330098_), s, p_329231_));
            }).toList();
        };
    }

    private static GameTestBatch toGameTestBatch(List<GameTestInfo> pTests, String pFunctionName, long pTime) {
        Consumer<ServerLevel> consumer = GameTestRegistry.getBeforeBatchFunction(pFunctionName);
        Consumer<ServerLevel> consumer1 = GameTestRegistry.getAfterBatchFunction(pFunctionName);
        return new GameTestBatch(pFunctionName + ":" + pTime, pTests, consumer, consumer1);
    }
}