package ru.sbt.util.HTTPDatapool.connectionInterface;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ru.sbt.util.HTTPDatapool.controllers.dto.AddingStatus;
import ru.sbt.util.HTTPDatapool.controllers.dto.CacheTableInfo;
import ru.sbt.util.HTTPDatapool.threadpools.AsyncDownloadPool;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class TablesCacheImpl implements TablesCache {

    @Autowired
    @Setter
    private DBRepository dbRepository;

    @Autowired
    @Setter
    private AsyncDownloadPool downloadPool;

    private ConcurrentHashMap<String, TableDownloader> cache = new ConcurrentHashMap<>();

    @Value("${countRowsOneSelect:1000}")
    @Setter
    private int countRowsOneSelect;

    @Override
    public Optional<List<Map<String, Object>>> getDataFromCache(String tableName, Set<String> columnNames) {
        log.debug("getDataFromCache() tableName:{}, columnNames:{}", tableName, columnNames);

        TableDownloader downloader = cache.get(tableName);
        if (downloader == null) {
            TableDownloader tableDownloader = new TableDownloader(downloadPool.getService(), dbRepository, tableName, countRowsOneSelect);
            TableDownloader ifAbsent = cache.putIfAbsent(tableName, tableDownloader);
            if (ifAbsent == null) {
                downloader = tableDownloader;
                downloader.putInCache();
            } else {
                downloader = ifAbsent;
            }
        }

        Optional<List<Map<String, Object>>> table = Optional.ofNullable(downloader.getData());
        if (!columnNames.contains("*")) {
            table = table.map(maps -> filterColumns(maps, columnNames));
        }

        return table;
    }

    @Override
    public void clearAllCaches() {
        cache.values().stream().filter(Objects::nonNull).forEach(TableDownloader::deleteFromCache);
        cache.clear();

    }

    @Override
    public void clearCache(String tableName) {
        TableDownloader downloader = cache.get(tableName);
        if (downloader != null) {
            downloader.deleteFromCache();
        }
        cache.remove(tableName);

    }

    @Override
    public int getCountRowsInCachedTable(String tableName) {
        if (cache.containsKey(tableName)) {
            List<Map<String, Object>> data = cache.get(tableName).getData();
            if (data != null) {
                return data.size();
            }
            return -1;
        } else {
            return -1;
        }
    }

    @Override
    public double getLoadedPercent(String tableName) {
        TableDownloader downloader = cache.get(tableName);
        if (downloader != null) {
            return downloader.getDownloadProgress();
        }
        return 0;
    }

    @Override
    public List<String> getAllTableNamesInCache() {

        return cache.values().stream()
                .filter(TableDownloader::isReady)
                .map(TableDownloader::getTableName)
                .collect(Collectors.toList());
    }

    @Override
    public List<CacheTableInfo> getAllInfoAboutTablesInCache() {

        return cache.values().stream()
                .map(this::createCacheTableInfo)
                .collect(Collectors.toList());

    }

    private CacheTableInfo createCacheTableInfo(TableDownloader tableDownloader) {
        // TODO: 01.02.2018 Написать тест проверяющий статусы закачки
        AddingStatus status;
        double downloadProgress = tableDownloader.getDownloadProgress();
        if (downloadProgress == 0) {
            status = AddingStatus.NEW;
        } else if (downloadProgress == 1) {
            status = AddingStatus.READY;
        } else {
            status = AddingStatus.UPDATING;
        }

        return CacheTableInfo.builder()
                .name(tableDownloader.getTableName())
                .progressDownload(tableDownloader.getDownloadProgress())
                .rowCount(tableDownloader.getDownloadedRowCount())
                .status(status)
                .build();
    }


    private List<Map<String, Object>> filterColumns(List<Map<String, Object>> table, Set<String> columnNames) {
        return table.stream()
                .map(stringObjectMap -> filterTableByColumns(stringObjectMap, columnNames))
                .collect(Collectors.toList());
    }

    private Map<String, Object> filterTableByColumns(Map<String, Object> stringObjectMap, Set<String> columnNames) {

        return stringObjectMap.entrySet().stream()
                .filter(stringObjectEntry -> columnNames.contains(stringObjectEntry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}