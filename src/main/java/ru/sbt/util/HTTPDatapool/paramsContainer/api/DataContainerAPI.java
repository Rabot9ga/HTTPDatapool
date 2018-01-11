package ru.sbt.util.HTTPDatapool.paramsContainer.api;

import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.util.List;
import java.util.Map;

public interface DataContainerAPI {

    RequestType requestType = null;

    /**
     * getting required data from pool using specified RequestType
     *
     * @return row with required data
     */
    Map<String, String> getRow();

    /**
     * adding data to structured pool
     */
//    <T extends Map<String, String>> void addRow(Map<String, String> t);
    void addRow(Map<String, String> row);

    <T extends List> void addTable(T collection);

    <T extends List> T getTable();

    int getSize();

    RequestType getRequestType();

}
