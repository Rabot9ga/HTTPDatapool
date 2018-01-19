package ru.sbt.util.HTTPDatapool.repository;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.sbt.util.HTTPDatapool.connectionInterface.DBConnection;

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
    public List<Map<String, String>> getDataFromCacheThreads(String tableName, Set<String> columnNames, int threadCount, int rowsCount, int rowsCountForOneThread) {



        return null;
    }

    @Override
    public List<Map<String, String>> getDataFromCacheBetween(String tableName, Set<String> columnNames, int from, int to) {

        if (cache.containsKey(tableName)) {
            cache.replace(tableName, getDataFromCacheBetween(tableName, columnNames, from, to));
        } else {
            cache.put(tableName, getFromTableBetween(tableName, from, to));
        }
        List<Map<String, String>> result = cache.get(tableName);

        return result;

    }

    @Override
    public void clearCache() {

        cache.clear();

    }

    private List<Map<String, String>> cachePut(String tableName) {
        List<Map<String, String>> table = getTable(tableName);
        cache.put(tableName, table);
        return table;
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
        if(value!=null){
            return value.toString();
        }
        return "";
    }
}
