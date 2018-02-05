package ru.sbt.util.HTTPDatapool.connectionInterface;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ru.sbt.util.HTTPDatapool.controllers.dto.AddingStatus;
import ru.sbt.util.HTTPDatapool.controllers.dto.CacheTableInfo;
import ru.sbt.util.HTTPDatapool.threadpools.AsyncDownloadPool;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

    private ConcurrentHashMap<String, List<Map<String, Object>>> cache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TableDownloader> partOfJob = new ConcurrentHashMap<>();

    @Value("${countRowsOneSelect:1000}")
    @Setter
    private int countRowsOneSelect;

    @Override
    public Optional<List<Map<String, Object>>> getDataFromCache(String tableName, Set<String> columnNames) {
        log.debug("getDataFromCache() tableName:{}, columnNames:{}", tableName, columnNames);

        Optional<List<Map<String, Object>>> table = Optional.ofNullable(cache.get(tableName));
//        log.debug("getTableFromCache: {}", table);

        if (!columnNames.contains("*")) {
            table = table.map(maps -> filterColumns(maps, columnNames));
//            log.debug("filteredTable: {}", table);
        }

        if (!table.isPresent()) {
            cachePut(tableName);
        }
        return table;
    }

    @Override
    public void clearAllCaches() {
        cache.clear();
        partOfJob.clear();
    }

    @Override
    public void clearCache(String tableName) {
        cache.remove(tableName);
        partOfJob.remove(tableName);
    }

    @Override
    public int getCountRowsInCachedTable(String tableName) {
        if (cache.containsKey(tableName)) {
            return cache.get(tableName).size();
        } else {
            return -1;
        }
    }

    @Override
    public double getLoadedPercent(String tableName) {
        if (partOfJob.containsKey(tableName)) {
            return partOfJob.get(tableName).getDownloadProgress();
        }
        return 0;
    }

    @Override
    public List<String> getAllTableNamesInCache() {
        return partOfJob.entrySet().stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<CacheTableInfo> getAllInfoAboutTablesInCache() {

        return partOfJob.entrySet().stream()
                .map(this::createCacheTableInfo)
                .collect(Collectors.toList());

    }

    private CacheTableInfo createCacheTableInfo(Map.Entry<String, TableDownloader> entry) {
        // TODO: 01.02.2018 Написать тест проверяющий статусы закачки
        AddingStatus status;
        double downloadProgress = entry.getValue().getDownloadProgress();
        if (downloadProgress == 0) {
            status = AddingStatus.NEW;
        } else if (downloadProgress == 1) {
            status = AddingStatus.READY;
        } else {
            status = AddingStatus.UPDATING;
        }

        return CacheTableInfo.builder()
                .name(entry.getKey())
                .progressDownload(entry.getValue().getDownloadProgress())
                .rowCount(entry.getValue().getDownloadedRowCount())
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

    private void cachePut(String tableName) {
        TableDownloader tableDownloader = new TableDownloader(cache, downloadPool.getService(), dbRepository, tableName, countRowsOneSelect);

        if (partOfJob.putIfAbsent(tableName, tableDownloader) == null) {
            log.debug("countThread: {}", downloadPool.getService().getMaximumPoolSize());
            tableDownloader.putInCache();
        }
    }

}