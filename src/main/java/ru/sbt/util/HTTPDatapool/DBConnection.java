package ru.sbt.util.HTTPDatapool;

import java.util.List;
import java.util.Map;

public interface DBConnection {
    List<Map<String, Object>>  getTable(String name);

}
