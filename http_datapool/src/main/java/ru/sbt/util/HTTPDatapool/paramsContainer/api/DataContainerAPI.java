package ru.sbt.util.HTTPDatapool.paramsContainer.api;

import ru.sbt.util.HTTPDatapool.httpapi.RequestType;

import java.util.List;
import java.util.Map;

public interface DataContainerAPI {

    /**
     * getting required data from pool using specified RequestType
     *
     * @return row with required data
     */
    Map<String, String> getRow();

    /**
     * adding data to structured pool
     */
    void addRow(Map<String, String> row);

    void addTable(List<Map<String, String>> collection);

    int getSize();

    RequestType getRequestType();

}
