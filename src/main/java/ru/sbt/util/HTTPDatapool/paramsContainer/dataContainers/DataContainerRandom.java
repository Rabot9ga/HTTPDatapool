package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import ru.sbt.util.HTTPDatapool.paramsContainer.api.ParamsContainerAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class DataContainerRandom implements ParamsContainerAPI {
    private List<Map<String, String>> list = new ArrayList<>();

    @Override
    public Map<String, String> getRow() {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    @Override
    public void addRow(Map<String, String> row) {
        list.add(row);
    }

    @Override
    public <T extends List> void addTable(T collection) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public <T extends List> T getTable() {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public int getSize() {
        return list.size();
    }

}
