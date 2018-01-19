package ru.sbt.util.HTTPDatapool.paramsContainer.utils;

import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.TestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class TestContainerUtils {

    public static TestResult gettingTest(List<String> list, int threadCount, int containerSize) {

        log.debug("Testing with {} threads, {} requests, {} listSize", threadCount, containerSize, list.size());

        Assert.assertTrue(list.size() == threadCount * containerSize, "Something has lost!");

        // number of occurrences when data is returning sequentially. At least one element is always sequential
        int numOfSequentials = 1;
        // number of repeats during two requests
        int numOfRepeats = 0;

        String prevValue = list.get(0);
        for (int i = 1; i < list.size(); i++) {

            String value = list.get(i);
            log.debug(value);
            if (value.equals(prevValue)) numOfRepeats++;
            if (Integer.parseInt(value) == ((Integer.parseInt(prevValue) + 1) % containerSize)) numOfSequentials++;

            prevValue = value;
        }

        return TestResult.builder()
                .numOfGets(list.size())
                .numOfRepeats(numOfRepeats)
                .numOfSequentials(numOfSequentials)
                .build();
    }

    public CopyOnWriteArrayList<String> concurrentGetRow(DataContainerAPI container, int threadCount, int containerSize) {
        ExecutorService service = Executors.newCachedThreadPool();

        List<Future> listFutures = new ArrayList<>(threadCount);

        CopyOnWriteArrayList<String> ids = new CopyOnWriteArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            listFutures.add(service.submit(() -> {
                for (int j = 0; j < containerSize; j++) {
                    synchronized (this){
                        String id = container.getRow().get("id");
                        ids.add(id);
                    }
                }
            }));
        }
        boolean allFuturesDone = false;

        while (!allFuturesDone) {
            allFuturesDone = listFutures.stream().allMatch(Future::isDone);
        }
        return ids;
    }


}
