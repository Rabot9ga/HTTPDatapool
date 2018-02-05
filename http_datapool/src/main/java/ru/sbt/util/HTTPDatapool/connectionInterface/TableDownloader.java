package ru.sbt.util.HTTPDatapool.connectionInterface;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class TableDownloader {
    private AtomicInteger passCountFuture = new AtomicInteger();
    private AtomicInteger failCountFuture = new AtomicInteger();

    @Getter
    private List<Map<String, Object>> data;

    private final ExecutorService service;
    private final DBRepository dbRepository;
    @Getter
    private final String tableName;
    @Getter
    private final int countRowsOneSelect;
    @Getter
    private volatile int countRows;
    @Getter
    private volatile int taskCount;
    @Getter
    private Map<String, Object>[] tmpLines;
    @Getter
    private ArrayList<CompletableFuture<Void>> futures;
    private volatile boolean abortedDownload;

    @Getter
    private volatile boolean isReady = false;

    public TableDownloader(ExecutorService service, DBRepository dbRepository, String tableName, int countRowsOneSelect) {
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


        futures = new ArrayList<>();

        for (int i = 0; i < taskCount; i++) {
            int from = i * countRowsOneSelect + 1;
            int to = (i + 1) * countRowsOneSelect;

            CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> dbRepository.getFromTableBetween(tableName, from, to), service)
                    .exceptionally(throwable -> exceptionally(tableName, from, to, throwable))
                    .thenAccept(list -> putValue(list, from, tableName));

            futures.add(future);
        }
    }

    public void deleteFromCache() {
        futures.stream()
                .filter(future -> !future.isDone())
                .forEach(future -> future.cancel(false));
        abortedDownload = true;
    }

    public double getDownloadProgress() {
        if (!abortedDownload) {
            int passCount = passCountFuture.get();
            int failCount = failCountFuture.get();
            return (double) (passCount + failCount) / (double) taskCount;
        }
        return -1.0;
    }

    public int getDownloadedRowCount() {
        int passCount = passCountFuture.get();
        int downloadedCount = passCount * countRowsOneSelect;

        if (downloadedCount > countRows) return countRows;

        return downloadedCount;
    }

    private void putValue(List<Map<String, Object>> list, int from, String tableName) {
        int i = from - 1;
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
            log.info("Table: {} downloaded", tableName);
            List<Map<String, Object>> listMap;
            if (failCount > 0) {
                listMap = Arrays.stream(tmpLines)
                        .parallel()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            } else {
                listMap = Arrays.asList(tmpLines);
            }
            data = listMap;
            isReady = true;
        }
    }

    private List<Map<String, Object>> exceptionally(String tableName, int from, int to, Throwable throwable) {
        log.error("Can't get batch from database, tableName:{}, row from:{} to:{}", tableName, from, to, throwable);
        failCountFuture.incrementAndGet();
        return Collections.emptyList();
    }
}
