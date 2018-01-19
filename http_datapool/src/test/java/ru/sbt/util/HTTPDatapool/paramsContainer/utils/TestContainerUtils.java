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
//            log.debug(value);
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

//    public static TestResult gettingTest(DataContainerAPI dataContainer, int size) {
//        int checkFactor;
//
//        if (size < 20) checkFactor = 4;
//        else checkFactor = 1;
//
//        // number of gets from dataContainer
//        int numOfGets = dataContainer.getSize() * checkFactor;
//
//        // number of occurrences when data is returning sequentially
//        int numOfSequentials = 0;
//
//        // number of repeats during two requests
//        int numOfRepeats = 0;
//
////        if (dataContainer.getSize() != size) {
////            throw new AssertionError("paramsContainer size is " + dataContainer.getSize() + " but must be " + size + "!");
////        }
//
//        Map<String, String> row0 = dataContainer.getRow();
//        String prevValue = row0.get("id");
//        log.trace("In test of {}. Returned id: {}", dataContainer.getRequestType(), row0.get("id"));
//
//
//        Map<String, String> row;
//        for (int i = 0; i < numOfGets; i++) {
//
//            row = dataContainer.getRow();
//
//            log.trace("In test of {}. Returned id: {}", dataContainer.getRequestType(), row.get("id"));
//
//            if (row.get("id").equals(prevValue)) numOfRepeats++;
//
//            log.trace("currRow id: {} / prevValue id: {}", row.get("id"), prevValue);
//
//            if (Integer.parseInt(row.get("id")) == ((Integer.parseInt(prevValue) + 1) % size)) {
//                numOfSequentials++;
//            }
//
//            prevValue = row.get("id");
//        }
//
//        log.trace("\n == {} == \n NumOfGets: {} \n Number of how many times data returned sequentially = {} " +
//                "\n numOfRepeats : {}", dataContainer.getRequestType(), numOfGets, numOfSequentials, numOfRepeats);
//
//        TestResult testResult = TestResult.builder()
//                .numOfGets(numOfGets)
//                .numOfRepeats(numOfRepeats)
//                .numOfSequentials(numOfSequentials)
//                .build();
//
//        return testResult;
//    }
//
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
