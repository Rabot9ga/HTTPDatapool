package ru.sbt.util.HTTPDatapool.connectionInterface;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class DBRepositoryOracle implements DBRepository {


    private JdbcTemplate jdbcTemplate;

    public DBRepositoryOracle(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Map<String, Object>> getFromTableBetween(String tableName, int from, int to) {
        String query = "SELECT * FROM (SELECT ROWNUM NUM, A.* FROM " + tableName + " A) B ";
        query += "WHERE B.NUM BETWEEN " + from + " and " + to;
        return jdbcTemplate.queryForList(query);
    }

    @Override
    public int getTableSize(String tableName) {
        return Integer.parseInt(jdbcTemplate.queryForObject("SELECT COUNT (*) FROM " + tableName, String.class));
    }

    @Override
    public List<String> getAllTableNamesInDB() {
        return jdbcTemplate.queryForList("select table_name from User_TABLES").stream()
                .map(entry -> entry.get("TABLE_NAME").toString())
                .collect(Collectors.toList());
    }
}
