package ru.sbt.util.HTTPDatapool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class DataRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<Map<String, Object>> getTable(String tableName) {
        List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT * FROM "+tableName);
        return result;

    }
}
