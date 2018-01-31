package ru.sbt.util.HTTPDatapool.connectionInterface;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.Generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

@Slf4j
public class TableDownloaderTest {

    ConcurrentHashMap<String, List<Map<String, Object>>> cache = new ConcurrentHashMap<>();
    ExecutorService service = Executors.newFixedThreadPool(50);


    private DBRepository dbRepository;
    private String tableName;
    private int countRowsOneSelect;
    private TableDownloader downloader;
    private List<Map<String, Object>> table;

    @BeforeClass
    public void setUpClass() throws Exception {
        dbRepository = mock(DBRepository.class);
        tableName = "Test";
        countRowsOneSelect = 3600;
        int rowInTable = 1000000;

        when(this.dbRepository.getTableSize(tableName)).thenReturn(rowInTable);

        table = new ArrayList<>();
        for (int i = 0; i < rowInTable / countRowsOneSelect + 1; i++) {
            int from = countRowsOneSelect * i + 1;
            int to = countRowsOneSelect * (i + 1);

            int deviation = 0;
            if (to > rowInTable) {
                deviation = to - rowInTable;
            }
            List<Map<String, Object>> list = Generator.fillDataList(countRowsOneSelect - deviation);
            table.addAll(list);
            when(this.dbRepository.getFromTableBetween(tableName, from, to - deviation)).thenReturn(list);
        }

    }

    @BeforeMethod
    public void setUp() throws Exception {
        downloader = new TableDownloader(cache, service, dbRepository, tableName, countRowsOneSelect);

    }

    @Test
    public void testPutInCache() throws Exception {
        downloader.putInCache();
        double progress = downloader.getDownloadProgress();
        while (progress < 1.0) {
            progress = downloader.getDownloadProgress();
        }
        Thread.sleep(1);

        assertEquals(cache.mappingCount(), 1);
        List<Map<String, Object>> maps = cache.get(tableName);

        assertEquals(table, tableName);
    }

    @Test
    public void testGetDowloadedRowCount() throws Exception {
        downloader.putInCache();

        int dowloadedRowCount = downloader.getDowloadedRowCount();
        Thread.sleep(1);
        int dowloadedRowCount1 = downloader.getDowloadedRowCount();

        log.debug("dowloadedRowCount: {}", dowloadedRowCount);
        log.debug("dowloadedRowCount1: {}", dowloadedRowCount1);

        assertNotEquals(dowloadedRowCount, dowloadedRowCount1);
    }
}