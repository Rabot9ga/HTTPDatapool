package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class DataContainerRandom extends AbstractDataContainer implements DataContainerAPI {
    private List<Map<String, String>> list = new ArrayList<>();

    public DataContainerRandom(Collection collection) {
        super.requestType = RequestType.RANDOM;

        // FIXME: 12.01.2018 Which type is preferable?

        if (collection != null) {
            this.addTable((ArrayList) collection);
        }
    }

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
//        list.add(collection)
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

    @Override
    public RequestType getRequestType() {
        return super.requestType;
    }

}
