package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import ru.sbt.util.HTTPDatapool.paramsContainer.utils.Generator;

import java.util.List;
import java.util.Map;

public abstract class TestData extends TestSettings{
    // list used in addTable
    List<Map<String, String>> list = Generator.fillDataList(containerSize);



}
