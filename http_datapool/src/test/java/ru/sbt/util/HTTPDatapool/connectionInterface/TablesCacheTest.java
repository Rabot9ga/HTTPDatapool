package ru.sbt.util.HTTPDatapool.connectionInterface;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;
import ru.sbt.util.HTTPDatapool.controllers.dto.AddingStatus;
import ru.sbt.util.HTTPDatapool.controllers.dto.CacheTableInfo;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@SpringBootTest(properties = "logging.level.ru.sbt.util.HTTPDatapool=debug")
@Slf4j
public class TablesCacheTest extends AbstractTransactionalTestNGSpringContextTests {
    // TODO: 05.02.2018 Добавить embedded базу данных в тесты

    @Autowired
    TablesCache tablesCache;

    String tableName = "FORTEST";

    @Test(timeOut = 5000)
    public void testGetDataFromCache() throws Exception {
        List<Map<String, Object>> table = fillCache(tableName);
        assertEquals(table.size(), 10010);
    }

    @Test(timeOut = 5000)
    public void testClearAllCaches() throws Exception {
        fillCache(tableName);
        tablesCache.clearAllCaches();
        List<CacheTableInfo> tablesInCache = tablesCache.getAllInfoAboutTablesInCache();
        assertEquals(tablesInCache.size(), 0);

    }

    @Test(timeOut = 5000)
    public void testClearCache() throws Exception {
        fillCache(tableName);
        tablesCache.clearCache(tableName);
        List<CacheTableInfo> tablesInCache = tablesCache.getAllInfoAboutTablesInCache();
        log.info("tablesInCache.size() = {}", tablesInCache.size());
        log.info("tablesInCache = {}", tablesInCache);
        assertEquals(tablesInCache.size(), 0);
    }

    @Test(timeOut = 5000)
    public void testClearDownloadingCache() throws Exception {
        tablesCache.getDataFromCache(tableName, Collections.singleton("*"));
        tablesCache.clearCache(tableName);
        List<CacheTableInfo> tablesInCache = tablesCache.getAllInfoAboutTablesInCache();
        log.info("tablesInCache.size() = {}", tablesInCache.size());
        log.info("tablesInCache = {}", tablesInCache);
        assertEquals(tablesInCache.size(), 0);
    }

    @Test(timeOut = 5000)
    public void testGetCountRowsInCachedTable() throws Exception {
        fillCache(tableName);
        int rowsInCachedTable = tablesCache.getCountRowsInCachedTable(tableName);
        assertEquals(rowsInCachedTable, 10010);
    }

    @Test(timeOut = 5000)
    public void testGetLoadedPercent() throws Exception {
        tablesCache.getDataFromCache(tableName, Collections.singleton("*"));

        for (int i = 0; i < 100; i++) {
            double percent = tablesCache.getLoadedPercent(tableName);
            log.info("percent = {}", percent);
            assertTrue(percent >= 0.0 && percent <= 1.0);
            if (percent == 1.0) break;
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }

    @Test(timeOut = 5000)
    public void testGetAllTableNamesInCache() throws Exception {
        fillCache(tableName);
        fillCache("FORTEST2");
        List<String> namesInCache = tablesCache.getAllTableNamesInCache();
        String[] tables = {"FORTEST", "FORTEST2"};
        List<String> strings = Arrays.asList(tables);
        assertEquals(namesInCache, strings);
    }

    @Test(timeOut = 5000)
    public void testGetAllInfoAboutDownloadingTablesInCache() throws Exception {
        tablesCache.getDataFromCache(tableName, Collections.singleton("*"));
        List<CacheTableInfo> allInfoAboutTablesInCache = tablesCache.getAllInfoAboutTablesInCache();
        assertEquals(allInfoAboutTablesInCache.size(), 1);
        CacheTableInfo tableInfo = allInfoAboutTablesInCache.get(0);

        AddingStatus status = tableInfo.getStatus();
        assertEquals(status, AddingStatus.NEW);

        assertEquals(tableInfo.getName(), tableName);

        double progress = tableInfo.getProgressDownload();
        assertTrue(progress >= 0.0 && progress <= 1.0);

        int rowCount = tableInfo.getRowCount();
        assertTrue(rowCount >= 0 && rowCount <= 10010);
    }

    @Test(timeOut = 5000)
    public void testGetAllInfoDownloadedTablesInCache() throws Exception {
        fillCache(tableName);
        fillCache("FORTEST2");

        List<CacheTableInfo> allInfoAboutTablesInCache = tablesCache.getAllInfoAboutTablesInCache();
        log.info("allInfoAboutTablesInCache = {}", allInfoAboutTablesInCache);
        CacheTableInfo fortest = CacheTableInfo.builder().name("FORTEST").rowCount(10010).status(AddingStatus.READY).progressDownload(1.0).build();
        CacheTableInfo fortest2 = CacheTableInfo.builder().name("FORTEST2").rowCount(10).status(AddingStatus.READY).progressDownload(1.0).build();
        ArrayList<CacheTableInfo> cacheTableInfos = new ArrayList<>();
        cacheTableInfos.add(fortest);
        cacheTableInfos.add(fortest2);

        assertEquals(allInfoAboutTablesInCache, cacheTableInfos);
    }

    private List<Map<String, Object>> fillCache(String tableName) throws InterruptedException {
        Optional<List<Map<String, Object>>> test_table = Optional.empty();

        while (!test_table.isPresent()) {
            test_table = tablesCache.getDataFromCache(tableName, Collections.singleton("*"));
            log.info("test_table.isPresent() = {}", test_table.isPresent());
            TimeUnit.MILLISECONDS.sleep(100);
        }

        return test_table.orElse(new ArrayList<>());
    }
}