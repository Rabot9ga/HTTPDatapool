package ru.sbt.util.HTTPDatapool.connectionInterface;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest(properties = "logging.level.ru.sbt.util.HTTPDatapool=debug")
@Slf4j
public class TablesCacheTest extends AbstractTransactionalTestNGSpringContextTests {
    // TODO: 31.01.2018 Дописать тесты
    @Autowired
    TablesCache tablesCache;

    @Test
    public void testGetDataFromCache() throws Exception {
        Optional<List<Map<String, Object>>> test_table = tablesCache.getDataFromCache("TEST_TABLE", Collections.singleton("*"));

        System.out.println("test_table.isPresent() = " + test_table.isPresent());


    }

    @Test
    public void testClearAllCaches() throws Exception {

    }

    @Test
    public void testClearCache() throws Exception {
        // TODO: 31.01.2018 правильно проверить очистку кешей
    }

    @Test
    public void testGetCountRowsInCachedTable() throws Exception {
    }

    @Test
    public void testGetLoadedPercent() throws Exception {
    }

    @Test
    public void testGetAllTableNamesInCache() throws Exception {
    }

    @Test
    public void testGetAllTableNamesInDB() throws Exception {
    }

    @Test
    public void testGetAllInfoAboutTablesInCache() throws Exception {
    }
}