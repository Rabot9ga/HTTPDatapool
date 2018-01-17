package ru.sbt.util.HTTPDatapool.connectionInterface;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Repository
public class DataRepository implements DBConnection {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ConcurrentHashMap<String, List<Map<String, String>>> cache = new ConcurrentHashMap<>();


    private List<Map<String, String>> getTable(String tableName) throws DataAccessException {
        return jdbcTemplate.queryForList("SELECT * FROM " + tableName).stream()
                .map(this::castMapValue)
                .collect(Collectors.toList());

    }

    private int getTableSize(String tableName) throws DataAccessException {

        return Integer.parseInt(jdbcTemplate.queryForObject("SELECT COUNT (*) FROM " + tableName, String.class));

    }


    private List<Map<String, String>> getFromTableBetween(String tableName, int from, int to) throws DataAccessException {

        String query = "SELECT * FROM (SELECT ROWNUM NUM, A.* FROM " + tableName + " A) B ";
        query += "WHERE B.NUM BETWEEN " + from + " and " + to;
        return jdbcTemplate.queryForList(query).stream()
                .map(this::castMapValue)
                .collect(Collectors.toList());
    }


    @Override
    public List<Map<String, String>> getDataFromCache(String tableName, Set<String> columnNames) {


        List<Map<String, String>> table = cache.entrySet().stream()
                .filter(entry -> entry.getKey().equals(tableName))
                .map(Map.Entry::getValue)
                .findAny()
                .orElseGet(() -> cachePut(tableName));

        return table.stream()
                .map(stringStringMap -> filterTableByColumns(stringStringMap, columnNames))
                .collect(Collectors.toList());


    }


    @Override
    public void clearAllCaches() {

        cache.clear();

    }

    @Override
    public void clearCache(String tableName) {

    }

    private List<Map<String, String>> cachePut(String tableName) {


        Map<String, String>[] table = new Map[getTableSize(tableName)];
        int size = table.length;
//        List<Map<String, String>> table = getTable(tableName);
//        cache.put(tableName, table);
        return null;
    }

    private Map<String, String> filterTableByColumns(Map<String, String> stringStringMap, Set<String> columnNames) {
        return stringStringMap.entrySet().stream()
                .filter(stringStringEntry -> columnNames.contains(stringStringEntry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    private Map<String, String> castMapValue(Map<String, Object> map) {

        return map.entrySet().stream()
                .map(entry -> ImmutablePair.of(entry.getKey(), castValue(entry.getValue())))
                .collect(Collectors.toMap(o -> o.left, o -> o.right));
    }

    private String castValue(Object value) {
        if (value != null) {
            return value.toString();
        }
        return "";
    }
}
