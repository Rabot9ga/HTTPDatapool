package ru.sbt.util.HTTPDatapool;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;
import ru.sbt.util.HTTPDatapool.connectionInterface.DataRepository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertTrue;


@SpringBootTest
public class DataRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {


    @Autowired
    DataRepository dataRepository;


    @Test
    public void getSomeColumnFromTable() {


        HashSet<String> columnNames = new HashSet<>(Arrays.asList("PARTY_ID", "GIVEN_NAME", "MIDDLE_NAME", "FAMILY_NAME"));
        List<Map<String, String>> result = dataRepository.getDataFromCache("TEST_TABLE", columnNames);

        assertTrue(result.size() > 0);

    }

    @Test
    public void getSomeColumnFromTableThread() {
        HashSet<String> columnNames = new HashSet<>(Arrays.asList("PARTY_ID", "GIVEN_NAME", "MIDDLE_NAME", "FAMILY_NAME"));
        CustomizableThreadFactory selectThread = new CustomizableThreadFactory("selectThread");
        int countRows = 11;
        int countRowsOneSelect = 1;
        int countThread = 10;
        ExecutorService service = Executors.newFixedThreadPool(countThread, selectThread);


        long start = System.nanoTime();

        List<Future<List<Map<String, String>>>> futures = new ArrayList<>();
        for (int i = 0; i < countRows / countRowsOneSelect; i++) {
            int from = i * countRowsOneSelect + 1;
            int to = (i + 1) * countRowsOneSelect;
            //  futures.add(service.submit(() -> dataRepository.getDataFromCacheBetween("TEST_TABLE", columnNames, from, to)));
        }

        List<Map<String, String>> result = new ArrayList<>();
        for (Future<List<Map<String, String>>> future : futures) {
            try {
                while (future.get().isEmpty()) {

                }

                result.addAll(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        long stop = System.nanoTime();

        logger.info("Test time: " + (stop - start) / 1000000000 + " seconds");


    }

}