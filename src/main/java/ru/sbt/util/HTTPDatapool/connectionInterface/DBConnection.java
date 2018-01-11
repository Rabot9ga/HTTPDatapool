package ru.sbt.util.HTTPDatapool.connectionInterface;

import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DBConnection {
    List<Map<String, Object>>  getTable(String tableName) throws DataAccessException;
    List<Map<String, Object>>  getSomeColumnFromTable(String tablename, Set<String> columnNames) throws DataAccessException;

}
