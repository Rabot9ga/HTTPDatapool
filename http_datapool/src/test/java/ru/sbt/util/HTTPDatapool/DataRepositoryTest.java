package ru.sbt.util.HTTPDatapool;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;
import ru.sbt.util.HTTPDatapool.connectionInterface.DataRepository;


@SpringBootTest
@Slf4j

public class DataRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {


    @Autowired
    DataRepository dataRepository;


    @Test
    public void getSomeColumnFromTable() {

//        HashSet<String> columnNames = new HashSet<>(Arrays.asList("*"));
//        Optional<List<Map<String, Object>>> result = dataRepository.getDataFromCache("FORTEST", columnNames);
//        assertTrue(result.isPresent());
//        assertTrue(result.get().size() > 0);

    }
    @Test
    public void getAllTableFromDB() {

//
//        List<String> allTableNamesInDB = dataRepository.getAllTableNamesInDB();
//        assertTrue(allTableNamesInDB.contains("FORTEST"));

    }

     @Test
    public void getSomeColumnFromTableThread() {


//        HashSet<String> columnNames = new HashSet<>(Arrays.asList("PARTY_ID", "FAMILY_NAME"));
//
//
//        long start = System.currentTimeMillis();
//        List<Map<String, Object>> result = dataRepository.getDataFromCache("TEST_TABLE", columnNames);
//
//        log.info("Request from table time: {} second", (double)((System.currentTimeMillis() - start) / 1000));
//        log.info("========================================================");
//
//        start = System.currentTimeMillis();
//        List<Map<String, Object>> result2 = dataRepository.getDataFromCache("TEST_TABLE", columnNames);
//        log.info("Request from cache time: {} miliseconds", ((System.currentTimeMillis() - start)));
//        log.info("========================================================");
//
//         HashSet<String> columnNames1 = new HashSet<>(Arrays.asList("PARTY_ID"));
//         List<Map<String, Object>> result1 = dataRepository.getDataFromCache("FORTEST", columnNames1);
//
//         List<Map<String, String>> allInfoAboutTablesInCache = dataRepository.getAllInfoAboutTablesInCache();
//
//         assertTrue(result.size() == dataRepository.getCountRowsInCachedTable("TEST_TABLE"));
//

    }



}
