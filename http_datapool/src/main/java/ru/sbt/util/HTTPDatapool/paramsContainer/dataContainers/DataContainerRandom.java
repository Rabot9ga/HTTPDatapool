package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import ru.sbt.util.HTTPDatapool.httpdto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DataContainerRandom<T> extends AbstractDataContainer implements DataContainerAPI<T> {
    private List<T> list = new ArrayList<>(5_000);

    public DataContainerRandom(List<T> collection) {
        super.requestType = RequestType.RANDOM;
        if (collection != null) {
            this.addTable(collection);
        }
    }

    @Override
    public T getRow() {
        if (list.isEmpty())
            throw new IllegalArgumentException("Container is empty");
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    @Override
    public void addRow(T row) {
        list.add(row);
    }

    @Override
    public void addTable(List<T> collection) {
        list = collection;
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
