package ru.sbt.util.HTTPDatapool.paramsContainer.utils;

import lombok.extern.slf4j.Slf4j;
import ru.sbt.util.HTTPDatapool.paramsContainer.DataContainerFactory;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

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
            HashMap<String, String> map = new HashMap<>();
            map.put("id", Integer.toString(i));
            map.put("name", "Vasya_" + ThreadLocalRandom.current().nextInt(100));
            map.put("phone", "7891-" + ThreadLocalRandom.current().nextInt(1000, 8999));

            container.addRow(map);
        }
        log.trace(container.toString());
        return container;
    }

}
