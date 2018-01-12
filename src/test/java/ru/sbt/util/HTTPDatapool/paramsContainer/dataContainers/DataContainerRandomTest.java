package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.sbt.util.HTTPDatapool.paramsContainer.DataContainerFactory;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.TestResult;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.Generator;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.TestUtils;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.*;

@Slf4j
public class DataContainerRandomTest extends TestData {
    @Test
    public void getRow() throws Exception {
        TestResult testResult = TestUtils.gettingTest(paramsContainerRandom, containerSize);

        assertTrue("data doesn't return Sequentially!", testResult.getNumOfSequentials() < testResult.getNumOfGets());
        if (containerSize > 1) {
            assertTrue("Returning is the same all the time!", testResult.getNumOfRepeats() < testResult.getNumOfGets());
        }
    }

    @Test
    public void addRow() throws Exception {
        // FIXME: 12.01.2018 it doesn't check if data is adding correctly

        DataContainerAPI container = Generator.fillDataContainerRandom(containerSizeRow);
        assertTrue(container.getSize() == containerSizeRow);
    }

    @Test
    public void addTable() throws Exception {
        assertFalse("DataContainerRandom size is 0!!", paramsContainerRandom.getSize() == 0);
        assertFalse("DataContainerRandom is empty!", paramsContainerRandom.getTable().isEmpty());
        assertTrue("Added List size is not equal to Container Table size!", list.size() == paramsContainerRandom.getTable().size());
        assertTrue("DataContainerRandom size is incorrect! " + paramsContainerRandom.getTable().size() +
                " not equals " + containerSize, paramsContainerRandom.getTable().size() == containerSize);
    }

    @Test
    public void getTable() throws Exception {
//        for (Map map : list) {
//            log.trace("list: ID: {} Name: {}", map.get("id"), map.get("name"));
//        }
//
//        DataContainerAPI container = DataContainerFactory.create(RequestType.RANDOM, list);
//
//        for (Map map : container.getTable()) {
//            log.trace("ID: {} Name: {}", map.get("id"), map.get("name"));
//        }

        assertTrue("DataContainerRandom size is incorrect! " + paramsContainerRandom.getTable().size() +
                " not equals " + containerSize, paramsContainerRandom.getTable().size() == containerSize);
        assertTrue("ID of first element in Table is not 0!", paramsContainerRandom.getTable().get(0).get("id").equals("0"));
    }

    @Test
    public void getSize() throws Exception {
        assertTrue("DataContainer returns wrong size!", containerSize == paramsContainerRandom.getSize());
    }

    @Test
    public void getRequestType() throws Exception {
        assertTrue("DataContainer returns wrong RequestType!", paramsContainerRandom.getRequestType() == RequestType.RANDOM);
    }

}