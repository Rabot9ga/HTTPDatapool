package ru.sbt.util.HTTPDatapool.connectionInterface;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Repository;
import ru.sbt.util.HTTPDatapool.controllers.dto.AddingStatus;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DataRepository implements DBConnection {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private ConcurrentHashMap<String, List<Map<String, String>>> cache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Double> partOfJob = new ConcurrentHashMap<>();
    @Value("${countRowsOneSelect:1000}")
    private int countRowsOneSelect;
    @Value("${countThread:100}")
    private int countThread;

    private CustomizableThreadFactory selectThread;
    private ExecutorService service;

    @PostConstruct
    private void init(){
         selectThread = new CustomizableThreadFactory("selectThread");
        service = Executors.newFixedThreadPool(countThread, selectThread);
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
        if (columnNames.contains("*")) {
            return table;
        }
        return table.stream()
                .map(stringStringMap -> filterTableByColumns(stringStringMap, columnNames))
                .collect(Collectors.toList());
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
            return partOfJob.get(tableName);
        }
        return 0;
    }

    @Override
    public List<String> getAllTableNames() {
        return partOfJob.entrySet().stream()
                .map(stringDoubleEntry -> stringDoubleEntry.getKey())
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, String>> getAllInfoAboutTablesInCache() {
        List<Map<String, String>> listInfo = new ArrayList<>();
        for (Map.Entry<String, Double> stringListEntry : partOfJob.entrySet()) {
            Map<String, String> tableInfo = new HashMap<>();
            Double aDouble = partOfJob.get(stringListEntry.getKey());
            tableInfo.put("name", stringListEntry.getKey());
            if (cache.get(stringListEntry.getKey()) != null) {
                tableInfo.put("rowCount", String.valueOf(cache.get(stringListEntry.getKey()).size()));
            } else {
                tableInfo.put("rowCount", "no information");
            }
            tableInfo.put("progress", String.valueOf(aDouble * 100));
            if (aDouble != 1) {
                tableInfo.put("status", AddingStatus.UPDATING.name());
            } else {
                tableInfo.put("status", AddingStatus.READY.name());
            }
            listInfo.add(tableInfo);
        }
        return listInfo;
    }


    private List<Map<String, String>> cachePut(String tableName) {
        log.info("countRowsOneSelect: {}", countRowsOneSelect);
        log.info("countThread: {}", countThread);
        if (partOfJob.putIfAbsent(tableName, 0d) != null) {
            return null;
        }

        int countRows = getTableSize(tableName);
        log.info("countRows: {}", countRows);

        List<Future<List<Map<String, String>>>> futures = new ArrayList<>();
        for (int i = 0; i < countRows / countRowsOneSelect + 1; i++) {
            int from = i * countRowsOneSelect + 1;
            int to = (i + 1) * countRowsOneSelect;
            futures.add(service.submit(() -> getFromTableBetween(tableName, from, to)));
        }
        int i = 0;
        List<Map<String, String>> result = new ArrayList<>();
        for (Future<List<Map<String, String>>> future : futures) {

            try {
                result.addAll(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            partOfJob.replace(tableName, (double) (i + 1) / ((double) futures.size()));
            log.info("Table {} loaded by {} percent", tableName, partOfJob.get(tableName) * 100);
            i++;
        }
        cache.put(tableName, result);
        return result;
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