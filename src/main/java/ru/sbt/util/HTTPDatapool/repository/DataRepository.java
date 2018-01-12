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
import java.util.stream.Collectors;

@Repository
public class DataRepository implements DBConnection {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, String>> getTable(String tableName) throws DataAccessException {
        return jdbcTemplate.queryForList("SELECT * FROM "+tableName).stream()
                .map(this::castMapValue)
                .collect(Collectors.toList());

    }

    @Override
    public List<Map<String, String>> getSomeColumnFromTable(String tablename, Set<String> columnNames) throws DataAccessException {
        String query = "SELECT ";
        for (String columnName : columnNames) {
            query+=columnName +", ";
        }
        query=query.substring(0,query.lastIndexOf(','));
        query+=" FROM "+tablename;
        return jdbcTemplate.queryForList(query).stream()
                .map(this::castMapValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, String>> getDataFromCache(String tablenamem, Set<String> columnNames) {
        return null;
    }

    private Map<String, String> castMapValue(Map<String, Object> map) {
        return map.entrySet().stream()
                .map(entry -> ImmutablePair.of(entry.getKey(), entry.getValue().toString()))
                .collect(Collectors.toMap(o -> o.left, o -> o.right));
    }
}
