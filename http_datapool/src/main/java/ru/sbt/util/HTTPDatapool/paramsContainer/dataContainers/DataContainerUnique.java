package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import ru.sbt.util.HTTPDatapool.httpapi.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class DataContainerUnique<T> extends AbstractDataContainer implements DataContainerAPI<T> {

    private Queue queue = new ConcurrentLinkedQueue<T>();

    public DataContainerUnique(RequestType type, Collection collection) {
        if (!(type.equals(RequestType.UNIQUE_RANDOM) || type.equals(RequestType.UNIQUE_SEQUENTIAL)))
            throw new RuntimeException("Wrong RequestType format!", new IllegalClassFormatException("Creating Unique Data Container you must choose between UNIQUE_RANDOM and UNIQUE_SEQUENTIAL"));
        super.requestType = type;
    }

    @Override
    public T getRow() {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public void addRow(T row) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public void addTable(List<T> collection) {
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
