package ru.sbt.util.HTTPDatapool.connectionInterface;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Repository
@Slf4j
public class DataRepository implements DBConnection {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private  ConcurrentHashMap<String, List<Map<String, String>>> cache = new ConcurrentHashMap<>();
//    private ExecutorService service = new ThreadPoolExecutor(0, 350,
//            60L, TimeUnit.SECONDS,
//            new SynchronousQueue<>());


    @Value("${countRowsOneSelect:1000}")
    private int countRowsOneSelect;
    @Value("${countThread:100}")
    private int countThread;

    public int getCountRowsOneSelect() {
        return countRowsOneSelect;
    }

    public DataRepository setCountRowsOneSelect(int countRowsOneSelect) {
        this.countRowsOneSelect = countRowsOneSelect;
        return this;
    }

    public int getCountThread() {
        return countThread;
    }

    public DataRepository setCountThread(int countThread) {
        this.countThread = countThread;
        return this;
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
        cache.remove(tableName);
    }

    @Override
    public int getCountRowsInCachedTable(String tableName) {
        if (cache.containsKey(tableName)) {
            return cache.get(tableName).size();
        }
        else {
            return -1;
        }
    }


    private List<Map<String, String>> cachePut(String tableName) {

        log.info("countRowsOneSelect: {}",countRowsOneSelect);
        log.info("countThread: {}",countThread);



        CustomizableThreadFactory selectThread = new CustomizableThreadFactory("selectThread");
        int countRows = getTableSize(tableName);
        log.info("countRows: {}",countRows);



       ExecutorService service = Executors.newFixedThreadPool(countThread, selectThread);


        List<Future<List<Map<String, String>>>> futures = new ArrayList<>();
        for (int i = 0; i < countRows / countRowsOneSelect +1; i++) {
            int from = i * countRowsOneSelect + 1;
            int to = (i + 1) * countRowsOneSelect;
            futures.add(service.submit(() -> getFromTableBetween(tableName, from, to)));
        }

        List<Map<String, String>> result = new ArrayList<>();
        for (Future<List<Map<String, String>>> future : futures) {
            try {
                result.addAll(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        cache.put(tableName,result);
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
