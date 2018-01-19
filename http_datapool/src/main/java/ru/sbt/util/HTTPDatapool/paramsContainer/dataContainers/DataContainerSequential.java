package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import lombok.extern.slf4j.Slf4j;
import ru.sbt.util.HTTPDatapool.httpapi.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
public class DataContainerSequential extends AbstractDataContainer implements DataContainerAPI {

    private Deque<Map<String, String>> queueIn = new ConcurrentLinkedDeque<>();
    private List<Map<String, String>> list = new ArrayList<>(1_000);

    public DataContainerSequential(List<Map<String, String>> collection) {
        super.requestType = RequestType.SEQUENTIAL;
        if (collection != null) {
            this.addTable(collection);
        }
    }

    @Override
    public Map<String, String> getRow() {

        Map<String, String> row;

        row = queueIn.poll();
        if (row == null) {
            synchronized (this) {
                row = queueIn.poll();
                if (row == null) {
                    //queueIn has ended. We swap queues with each other
                    queueIn.addAll(list);
                    row = queueIn.poll();
                }
            }
        }
        return row;
    }

    @Override
    public void addRow(Map<String, String> row) {
        list.add(row);
        queueIn.addLast(row);
    }

    @Override
    public void addTable(List<Map<String, String>> collection) {
        list.addAll(collection);
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
