package ru.sbt.util.HTTPDatapool.connectionInterface;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface DBConnection {


    List<Map<String, String>> getDataFromCache(String tableName, Set<String> columnNames);

    List<Map<String, String>> getDataFromCacheThreads(String tableName, Set<String> columnNames, int threadCount, int rowsCount, int rowsCountForOneThread);

    List<Map<String, String>> getDataFromCacheBetween(String tableName, Set<String> columnNames, int from, int to);

    void clearCache();


}
