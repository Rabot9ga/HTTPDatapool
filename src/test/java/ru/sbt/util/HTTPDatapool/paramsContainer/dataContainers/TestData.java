package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import ru.sbt.util.HTTPDatapool.paramsContainer.DataContainerFactory;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.Generator;

import java.util.List;
import java.util.Map;

public abstract class TestData extends TestSettings{
    // list used in addTable
    List<Map<String, String>> list = Generator.fillDataList(containerSize);

    DataContainerAPI paramsContainerRandom = DataContainerFactory.create(RequestType.RANDOM, list);
    DataContainerAPI paramsContainerSequential = DataContainerFactory.create(RequestType.SEQUENTIAL, list);
}
