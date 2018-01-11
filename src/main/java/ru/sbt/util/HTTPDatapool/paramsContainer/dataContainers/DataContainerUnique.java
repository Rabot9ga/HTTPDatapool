package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import ru.sbt.util.HTTPDatapool.paramsContainer.api.ParamsContainerAPI;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;


public class DataContainerUnique implements ParamsContainerAPI {

    ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue<Map<String, String>>();

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

}
