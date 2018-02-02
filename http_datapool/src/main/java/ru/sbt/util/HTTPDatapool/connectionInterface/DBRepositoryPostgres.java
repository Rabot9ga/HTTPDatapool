package ru.sbt.util.HTTPDatapool.connectionInterface;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class DBRepositoryPostgres implements DBRepository {

    private JdbcTemplate jdbcTemplate;

    public DBRepositoryPostgres(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Map<String, Object>> getFromTableBetween(String tableName, int from, int to) {

// TODO: 31.01.2018 Сделать параметризованый квери
        String query = "SELECT * FROM public.\"" + tableName + "\" LIMIT " + (to - from + 1) + " OFFSET " + (from - 1);
        log.debug("query={}", query);
        return jdbcTemplate.queryForList(query);
    }

    @Override
    public int getTableSize(String tableName) {
        return Integer.parseInt(jdbcTemplate.queryForObject("SELECT COUNT (*) FROM public.\"" + tableName + "\"", String.class));
    }

    @Override
    public List<String> getAllTableNamesInDB() {
        return jdbcTemplate.queryForList("SELECT table_name FROM information_schema.tables where table_schema='public'").stream()
                .map(entry -> entry.get("TABLE_NAME").toString())
                .collect(Collectors.toList());
    }
}
