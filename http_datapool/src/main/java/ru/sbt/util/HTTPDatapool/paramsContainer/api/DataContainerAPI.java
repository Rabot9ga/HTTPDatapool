package ru.sbt.util.HTTPDatapool.paramsContainer.api;

import ru.sbt.util.HTTPDatapool.httpapi.RequestType;

import java.util.List;

public interface DataContainerAPI<T> {

    /**
     * getting required data from pool using specified RequestType
     *
     * @return row with required data
     */
    T getRow();

    /**
     * adding data to structured pool
     */
    void addRow(T row);

    void addTable(List<T> collection);

    int getSize();

    RequestType getRequestType();

}
