package ru.sbt.util.HTTPDatapool.connectionInterface;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface DBConnection {


    List<Map<String, String>> getDataFromCache(String tableName, Set<String> columnNames);

    void clearAllCaches();

    void clearCache(String tableName);

    int getCountRowsInCachedTable(String tableName);

    double getLoadedPercent(String tableName);

    List<String> getAllTableNames();

}
