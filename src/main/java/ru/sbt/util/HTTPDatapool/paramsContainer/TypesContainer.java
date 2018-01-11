package ru.sbt.util.HTTPDatapool.paramsContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Class providing collections storing data
 *
 * @author SBT-Muravev-AA
 *
 */
public class TypesContainer {
    private List list = new ArrayList<>();
    private ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public ConcurrentLinkedQueue getQueue() {
        return queue;
    }

    public void setQueue(ConcurrentLinkedQueue queue) {
        this.queue = queue;
    }
}
