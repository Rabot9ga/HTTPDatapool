package ru.sbt.util.HTTPDatapool.paramsContainer;

import org.junit.Test;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.util.HashMap;

public class ContainerAddingTest {

    @Test
    public void addingTest(){

        ParamsContainer paramsContainer = new ParamsContainer(RequestType.RANDOM);

        HashMap<String, String> map = new HashMap<>();

        map.put("id", "12345567");
        map.put("name", "Vasya");

        paramsContainer.addRow(map);
    }
}
