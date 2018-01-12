package ru.sbt.util.HTTPDatapool.connectionInterface;

import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DBConnection {
    List<Map<String, String>>  getTable(String tableName) throws DataAccessException;
    List<Map<String, String>>  getSomeColumnFromTable(String tablename, Set<String> columnNames) throws DataAccessException;
    List<Map<String, String>>  getDataFromCache (String tablenamem, Set<String> columnNames);

}
