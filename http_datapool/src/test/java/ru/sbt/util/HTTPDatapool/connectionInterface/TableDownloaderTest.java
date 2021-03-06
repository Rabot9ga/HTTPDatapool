package ru.sbt.util.HTTPDatapool.connectionInterface;

import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
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

    ConcurrentHashMap<String, TableDownloader> cache = new ConcurrentHashMap<>();
    ExecutorService service = Executors.newFixedThreadPool(10);


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
            when(this.dbRepository.getFromTableBetween(tableName, from, to)).thenReturn(list);
        }

    }

    @BeforeMethod
    public void setUp() throws Exception {
        downloader = new TableDownloader(service, dbRepository, tableName, countRowsOneSelect);
        cache.put(tableName, downloader);
    }

    @Test
    public void testPutInCache() throws Exception {
        downloader.putInCache();
        double progress = downloader.getDownloadProgress();
        while (!downloader.isReady()) {

        }
        Thread.sleep(1);

        assertEquals(cache.mappingCount(), 1);
        TableDownloader downloader = cache.get(tableName);

        List<Map<String, Object>> data = downloader.getData();

        assertEquals(table, data);
    }

    @Test
    public void testGetDowloadedRowCount() throws Exception {
        downloader.putInCache();

        int dowloadedRowCount = downloader.getDownloadedRowCount();
        Thread.sleep(5);
        int dowloadedRowCount1 = downloader.getDownloadedRowCount();

        log.debug("dowloadedRowCount: {}", dowloadedRowCount);
        log.debug("dowloadedRowCount1: {}", dowloadedRowCount1);

        assertNotEquals(dowloadedRowCount, dowloadedRowCount1);
    }

    @Test
    public void testDeleteCache() throws Exception {
        downloader.putInCache();
        downloader.deleteFromCache();
        log.debug("futures: {}", downloader.getFutures());
        log.debug("downloader.getDownloadProgress() = {}", downloader.getDownloadProgress());
        Assert.assertTrue(downloader.getFutures().stream().allMatch(future -> future.isCancelled() || future.isDone()));
        Assert.assertEquals(downloader.getDownloadProgress(), -1.0);
    }
}