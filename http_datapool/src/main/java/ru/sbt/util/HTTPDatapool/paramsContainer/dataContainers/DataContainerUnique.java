package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import ru.sbt.util.HTTPDatapool.httpapi.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class DataContainerUnique extends AbstractDataContainer implements DataContainerAPI {

    private Queue queue = new ConcurrentLinkedQueue<Map<String, String>>();

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
    public void addTable(List<Map<String, String>> collection) {
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
