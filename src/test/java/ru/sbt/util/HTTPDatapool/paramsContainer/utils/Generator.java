package ru.sbt.util.HTTPDatapool.paramsContainer.utils;

import lombok.extern.slf4j.Slf4j;
import ru.sbt.util.HTTPDatapool.paramsContainer.ParamsContainer;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class Generator {

    static public ParamsContainer fillParamsContainerRandom(int size) {
        return fillParamsContainer(ParamsContainer.createRandom(), size);
    }
    static public ParamsContainer fillParamsContainerSequential(int size) {
        return fillParamsContainer(ParamsContainer.createSequential(), size);
    }

    static private ParamsContainer fillParamsContainer(ParamsContainer container, int size) {

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
