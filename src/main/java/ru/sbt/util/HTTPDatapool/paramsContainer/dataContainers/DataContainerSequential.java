package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import lombok.extern.slf4j.Slf4j;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class DataContainerSequential extends AbstractDataContainer implements DataContainerAPI {

    private List<Map<String, String>> list = new ArrayList<>();

    private AtomicInteger counter = new AtomicInteger();

    public DataContainerSequential() {
        counter.set(0);
        super.requestType = RequestType.SEQUENTIAL;
    }

    @Override
    public synchronized Map<String, String> getRow() {
        Map<String, String> map = list.get(counter.get());
//        log.debug("list.size() is: {} / counter is {} / get is:  {}", list.size(), counter, list.get(counter.get()));

        // FIXME: 11.01.2018 IT SEEMS TO BE SHITTY
        if (counter.get() + 1 == list.size()) {
            counter.set(0);
        } else {
            counter.getAndIncrement();
        }
        return map;
    }

    @Override
    public void addRow(Map<String, String> row) {
        list.add(row);
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
        return list.size();
    }

    @Override
    public RequestType getRequestType() {
        return super.requestType;
    }

}
