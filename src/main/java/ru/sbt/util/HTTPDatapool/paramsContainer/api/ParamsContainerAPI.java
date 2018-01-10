package ru.sbt.util.HTTPDatapool.paramsContainer.api;

import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.util.Map;

public interface ParamsContainerAPI {
    /**
     * @return type of requesting data from pool
     */
    RequestType getRequestType();

    /**
     * setting type of requesting data from pool
     */
    void setRequestType(RequestType type);

    /**
     * getting required data from pool using specified RequestType
     *
     * @param <T> type of returning data
     * @return row with required data
     */
    <T> T getRow();

    /**
     * adding data to structured pool
     *
     * @param t
     * @param <T>
     */
    <T extends Map<String, String>> void addRow(T t);
}
