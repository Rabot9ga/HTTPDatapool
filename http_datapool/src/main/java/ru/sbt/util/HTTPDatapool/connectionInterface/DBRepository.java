package ru.sbt.util.HTTPDatapool.connectionInterface;

import java.util.List;
import java.util.Map;

public interface DBRepository {
    List<Map<String, Object>> getFromTableBetween(String tableName, int from, int to);

    int getTableSize(String tableName);

    List<String> getAllTableNamesInDB();

}
