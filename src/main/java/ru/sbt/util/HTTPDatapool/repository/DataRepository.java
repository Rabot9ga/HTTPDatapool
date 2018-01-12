package ru.sbt.util.HTTPDatapool.repository;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.sbt.util.HTTPDatapool.connectionInterface.DBConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class DataRepository implements DBConnection {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Map<String, List<Map<String, String>>> cache = new ConcurrentHashMap<>();


    private List<Map<String, String>> getTable(String tableName) throws DataAccessException {
        return jdbcTemplate.queryForList("SELECT * FROM " + tableName).stream()
                .map(this::castMapValue)
                .collect(Collectors.toList());

    }


    private List<Map<String, String>> getSomeColumnFromTable(String tablename, Set<String> columnNames) throws DataAccessException {
        String query = "SELECT ";
        for (String columnName : columnNames) {
            query += columnName + ", ";
        }
        query = query.substring(0, query.lastIndexOf(','));
        query += " FROM " + tablename;
        return jdbcTemplate.queryForList(query).stream()
                .map(this::castMapValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, String>> getDataFromCache(String tableName, Set<String> columnNames, boolean updateFlag) {
        if (updateFlag) {
            cache.replace(tableName, getTable(tableName));
        }
        if (cache.containsKey(tableName)) {
            return resultConstruct(tableName, columnNames);
        } else {
            cache.put(tableName, getTable(tableName));
            return resultConstruct(tableName, columnNames);
        }

    }

    private List<Map<String, String>> resultConstruct(String tableName, Set<String> columnNames) {
        List<Map<String, String>> table = cache.entrySet().stream()
                .filter(entry -> entry.getKey().equals(tableName))
                .map(Map.Entry::getValue)
                .findAny()
                .orElse(cache.put(tableName, getTable(tableName)));

        return table.stream()
                .map(stringStringMap -> filterTableByColumns(stringStringMap, columnNames))
                .collect(Collectors.toList());

    }

    private Map<String, String> filterTableByColumns(Map<String, String> stringStringMap, Set<String> columnNames) {
        return stringStringMap.entrySet().stream()
                .filter(stringStringEntry -> columnNames.contains(stringStringEntry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    private Map<String, String> castMapValue(Map<String, Object> map) {
        return map.entrySet().stream()
                .map(entry -> ImmutablePair.of(entry.getKey(), entry.getValue().toString()))
                .collect(Collectors.toMap(o -> o.left, o -> o.right));
    }
}
