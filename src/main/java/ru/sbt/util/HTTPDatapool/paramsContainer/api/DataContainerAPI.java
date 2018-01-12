package ru.sbt.util.HTTPDatapool.paramsContainer.api;

import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.util.ArrayList;
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

    <T extends List> void addTable(List<Map<String, String>> collection);

    <T extends List> List<Map<String, String>> getTable();

    int getSize();

    RequestType getRequestType();

}
