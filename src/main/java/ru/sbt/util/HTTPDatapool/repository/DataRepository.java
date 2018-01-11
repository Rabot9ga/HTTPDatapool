package ru.sbt.util.HTTPDatapool.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.sbt.util.HTTPDatapool.connectionInterface.DBConnection;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class DataRepository implements DBConnection {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> getTable(String tableName) throws DataAccessException {
        List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT * FROM "+tableName);
        return result;

    }

    @Override
    public List<Map<String, Object>> getSomeColumnFromTable(String tablename, Set<String> columnNames) throws DataAccessException {
        String query = "SELECT ";
        for (String columnName : columnNames) {
            query+=columnName +", ";
        }
        query=query.substring(0,query.lastIndexOf(','));
        query+=" FROM "+tablename;
        List<Map<String, Object>> result = jdbcTemplate.queryForList(query);
        return result;
    }
}
