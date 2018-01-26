package ru.sbt.util.HTTPDatapool.connectionInterface;

import ru.sbt.util.HTTPDatapool.controllers.dto.CacheTableInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public interface TablesCache {

    Optional<List<Map<String, Object>>> getDataFromCache(String tableName, Set<String> columnNames);

    void clearAllCaches();

    void clearCache(String tableName);

    int getCountRowsInCachedTable(String tableName);

    double getLoadedPercent(String tableName);

    List<String> getAllTableNamesInCache();

    List<CacheTableInfo> getAllInfoAboutTablesInCache();
}
