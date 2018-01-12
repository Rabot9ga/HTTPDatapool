package ru.sbt.util.HTTPDatapool.paramsContainer.utils;

import lombok.extern.slf4j.Slf4j;
import ru.sbt.util.HTTPDatapool.paramsContainer.DataContainerFactory;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
public class Generator {
    static public DataContainerAPI fillDataContainerRandom(int size) {
        return fillParamsContainer(DataContainerFactory.create(RequestType.RANDOM), size);
    }

    static public DataContainerAPI fillDataContainerSequential(int size) {
        return fillParamsContainer(DataContainerFactory.create(RequestType.SEQUENTIAL), size);
    }

    static public DataContainerAPI fillDataContainerUniqueRandom(int size) {
        return fillParamsContainer(DataContainerFactory.create(RequestType.UNIQUE_RANDOM), size);
    }

    static public DataContainerAPI fillDataContainerUniqueSequential(int size) {
        return fillParamsContainer(DataContainerFactory.create(RequestType.UNIQUE_SEQUENTIAL), size);
    }

    static private DataContainerAPI fillParamsContainer(DataContainerAPI container, int size) {

        if (size <= 0) throw new NumberFormatException("Size must be positive!");

        for (int i = 0; i < size; i++) {
            container.addRow(fillDataMap(i));
        }
        log.trace(container.toString());
        return container;
    }

    static public HashMap<String, String> fillDataMap(int id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", Integer.toString(id));
        map.put("name", "Vasya_" + ThreadLocalRandom.current().nextInt(100));
        map.put("phone", "7891-" + ThreadLocalRandom.current().nextInt(1000, 8999));

        return map;
    }

    static public ArrayList<Map<String, String>> fillDataList(int rows) {
        ArrayList<Map<String, String>> list = new ArrayList<>();

        if (rows <= 0) throw new IllegalArgumentException("Number of rows must be > 0!");

        for (int i = 0; i < rows; i++) {
            list.add(fillDataMap(i));
        }

        return list;
    }

//    static public ArrayList<Map<String, String>> fillDataListStream(int rows) {
//
//        ArrayList<Map<String, String>> list = new ArrayList<>();
//        AtomicInteger ai = new AtomicInteger();
//        ai.set(0);
//
//        ArrayList<Map<String, String>> list = Stream.generate(null)
//                .limit(rows)
//                .forEach(Generator::fillDataMap);
//
//
//        if (rows <= 0) throw new IllegalArgumentException("Number of rows must be > 0!");
//
//        for (int i = 0; i < rows; i++) {
//            list.add(fillDataMap(i));
//        }
//
//        return list;
//    }

}
