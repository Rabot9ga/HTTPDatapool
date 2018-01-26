package ru.sbt.util.HTTPDatapool.connectionInterface;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class TableDownloader {
    private AtomicInteger passCountFuture = new AtomicInteger();
    private AtomicInteger failCountFuture = new AtomicInteger();

    private ConcurrentHashMap<String, List<Map<String, Object>>> cache;
    private ExecutorService service;
    private DBRepository dbRepository;
    private String tableName;
    private int countRows;
    private int countRowsOneSelect;

    private int taskCount;
    private Map<String, Object>[] tmpLines;

    public TableDownloader(ConcurrentHashMap<String, List<Map<String, Object>>> cache, ExecutorService service, DBRepository dbRepository, String tableName, int countRowsOneSelect) {
        this.cache = cache;
        this.service = service;
        this.dbRepository = dbRepository;
        this.tableName = tableName;
        this.countRowsOneSelect = countRowsOneSelect;
    }

    public void putInCache() {
        log.info("countRows: {}", countRows);
        log.info("countRowsOneSelect: {}", countRowsOneSelect);

        countRows = dbRepository.getTableSize(tableName);
        tmpLines = new Map[countRows];
        taskCount = countRows / countRowsOneSelect + 1;

        for (int i = 0; i < taskCount; i++) {
            int from = i * countRowsOneSelect + 1;
            int to = (i + 1) * countRowsOneSelect;

            CompletableFuture.supplyAsync(() -> dbRepository.getFromTableBetween(tableName, from, to), service)
                    .exceptionally(throwable -> exceptionally(tableName, from, to, throwable))
                    .thenAccept(list -> putValue(list, from, tableName));
        }
    }

    public double getDownloadProgress() {
        int passCount = passCountFuture.get();
        int failCount = failCountFuture.get();
        return (double) (passCount + failCount) / (double) taskCount;
    }

    public int getDowloadedRowCount(){
        int passCount = passCountFuture.get();
        return passCount * countRowsOneSelect;
    }

    private void putValue(List<Map<String, Object>> list, int from, String tableName) {
        int i = from;
        for (Map<String, Object> stringObjectMap : list) {
            tmpLines[i] = stringObjectMap;
            ++i;
        }
        int passCount = passCountFuture.incrementAndGet();
        int failCount = failCountFuture.get();

        if (log.isDebugEnabled()) {
            double percent = (double) (passCount + failCount) / ((double) taskCount);
            log.debug("Table {} loaded by {} percent", tableName, percent * 100);
        }

        if (passCount + failCount == taskCount) {
            List<Map<String, Object>> listMap;
            if (failCount > 0) {
                listMap = Arrays.stream(tmpLines)
                        .filter(Objects::isNull)
                        .collect(Collectors.toList());
            } else {
                listMap = Arrays.asList(tmpLines);
            }
            cache.putIfAbsent(tableName, listMap);
        }
    }

    private List<Map<String, Object>> exceptionally(String tableName, int from, int to, Throwable throwable) {
        log.error("Can't get batch from database, tableName:{}, row from:{} to:{}", tableName, from, to, throwable);
        failCountFuture.incrementAndGet();
        return Collections.emptyList();
    }
}
