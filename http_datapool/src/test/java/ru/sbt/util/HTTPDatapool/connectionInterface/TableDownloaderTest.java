package ru.sbt.util.HTTPDatapool.connectionInterface;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TableDownloaderTest {

    ConcurrentHashMap<String, List<Map<String, Object>>> cache = new ConcurrentHashMap<>();
    ExecutorService service = Executors.newFixedThreadPool(50);


    @BeforeMethod
    public void setUp() throws Exception {
        new TableDownloader(cache, service, );
    }

    @Test
    public void testPutInCache() throws Exception {
    }

    @Test
    public void testGetDownloadProgress() throws Exception {
    }

    @Test
    public void testGetDowloadedRowCount() throws Exception {
    }
}