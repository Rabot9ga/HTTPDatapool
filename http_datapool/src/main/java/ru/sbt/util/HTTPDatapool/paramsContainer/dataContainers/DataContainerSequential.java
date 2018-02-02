package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import lombok.extern.slf4j.Slf4j;
import ru.sbt.util.HTTPDatapool.httpdto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
public class DataContainerSequential<T> extends AbstractDataContainer implements DataContainerAPI<T> {

    private Deque<T> queueIn = new ConcurrentLinkedDeque<>();
    private List<T> list = new ArrayList<>(1_000);

    public DataContainerSequential(List<T> collection) {
        super.requestType = RequestType.SEQUENTIAL;
        if (collection != null) {
            this.addTable(collection);
        }
    }

    @Override
    public T getRow() {

        T row;
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
    public void addRow(T row) {
        list.add(row);
        queueIn.addLast(row);
    }

    @Override
    public void addTable(List<T> collection) {
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
