package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import lombok.extern.slf4j.Slf4j;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class DataContainerSequential extends AbstractDataContainer implements DataContainerAPI {

//    private List<Map<String, String>> list = new ArrayList<>(500_000);
//    private List<Map<String, String>> list = new CopyOnWriteArrayList();
//    private List<Map<String, String>> list = Collections.synchronizedList(new ArrayList<>(500_000));
    private Deque<Map<String, String>> queueIn = new ConcurrentLinkedDeque<>();
    private Deque<Map<String, String>> queueOut = new ConcurrentLinkedDeque();

    private AtomicInteger counter = new AtomicInteger();

    public DataContainerSequential(List<Map<String, String>> collection) {
        counter.set(0);
        super.requestType = RequestType.SEQUENTIAL;
        if (collection != null) {
            this.addTable(collection);
        }
    }

    @Override
    public Map<String, String> getRow() {

        Map<String, String> row;

        synchronized (this){
            row = queueIn.poll();
            if (row == null) {
                //queueIn has ended. We swap queues with each other
                Deque<Map<String, String>> queueTemp = queueOut;
                queueOut = queueIn;
                queueIn = queueTemp;

                row = queueIn.poll();
            }
            queueOut.addLast(row);
        }
        return row;
    }

    @Override
    public void addRow(Map<String, String> row) {
        queueIn.add(row);
    }

    @Override
    public void addTable(List<Map<String, String>> collection) {
        queueIn.addAll(collection);
    }

    @Override
    public int getSize() {
        return queueIn.size();
    }

    @Override
    public RequestType getRequestType() {
        return super.requestType;
    }

}
