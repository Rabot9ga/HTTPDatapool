package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;


public class DataContainerUnique extends AbstractDataContainer implements DataContainerAPI {

    ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue<Map<String, String>>();

    public DataContainerUnique(RequestType type, Collection collection) {
        if (!(type.equals(RequestType.UNIQUE_RANDOM) || type.equals(RequestType.UNIQUE_SEQUENTIAL)))
            throw new RuntimeException("Wrong RequestType format!", new IllegalClassFormatException("Creating Unique Data Container you must choose between UNIQUE_RANDOM and UNIQUE_SEQUENTIAL"));
        super.requestType = type;
    }

    @Override
    public Map<String, String> getRow() {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public void addRow(Map<String, String> row) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public <T extends List> T getTable() {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public <T extends List> void addTable(T collection) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public int getSize() {
        return queue.size();
    }

    @Override
    public RequestType getRequestType() {
        return super.requestType;
    }

}
