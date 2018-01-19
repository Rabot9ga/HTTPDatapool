package ru.sbt.util.HTTPDatapool;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;
import ru.sbt.util.HTTPDatapool.connectionInterface.DataRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;


@SpringBootTest
@Slf4j
public class DataRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {


    @Autowired
    DataRepository dataRepository;


    @Test
    public void getSomeColumnFromTable() {


//        HashSet<String> columnNames = new HashSet<>(Arrays.asList("PARTY_ID", "GIVEN_NAME", "MIDDLE_NAME", "FAMILY_NAME"));
        HashSet<String> columnNames = new HashSet<>(Arrays.asList("PARTY_ID"));
        List<Map<String, String>> result = dataRepository.getDataFromCache("FORTEST", columnNames);

        assertTrue(result.size() > 0);

    }

    @Test
    public void getSomeColumnFromTableThread() {


        HashSet<String> columnNames = new HashSet<>(Arrays.asList("PARTY_ID", "FAMILY_NAME"));


        long start = System.currentTimeMillis();
        List<Map<String, String>> result = dataRepository.getDataFromCache("TEST_TABLE", columnNames);

        log.info("Request from table time: {} second", (double)((System.currentTimeMillis() - start) / 1000));
        log.info("========================================================");

        start = System.currentTimeMillis();
        List<Map<String, String>> result2 = dataRepository.getDataFromCache("TEST_TABLE", columnNames);
        log.info("Request from cache time: {} miliseconds", ((System.currentTimeMillis() - start)));
        log.info("========================================================");

        assertTrue(result.size() == dataRepository.getCountRowsInCachedTable("TEST_TABLE"));


    }

}