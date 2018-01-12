package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.TestResult;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.Generator;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.TestUtils;

import static org.junit.Assert.*;

@Slf4j
public class DataContainerRandomTest {
    private int containerSize = 15;
    DataContainerAPI paramsContainerRandom = Generator.fillDataContainerRandom(containerSize);

    @Test
    public void getRow() throws Exception {
        TestResult testResult = TestUtils.gettingTest(paramsContainerRandom, containerSize);
        for (int i = 0; i < 30; i++) {
            log.debug(paramsContainerRandom.getRow().get("id"));
        }

        assertTrue("data doesn't return Sequentially!", testResult.getNumOfSequentials() < testResult.getNumOfGetts());
        if (containerSize > 1) {
            assertTrue("Returning is the same all the time!", testResult.getNumOfRepeats() < testResult.getNumOfGetts());
        }
    }

    @Test
    public void addRow() throws Exception {
        // FIXME: 12.01.2018 it doesn't check if data is adding correctly

        DataContainerAPI container = Generator.fillDataContainerRandom(containerSize);
        assertTrue(container.getSize() == container.getSize());
    }

//    @Test
//    public void addTable() throws Exception {
//    }
//
//    @Test
//    public void getTable() throws Exception {
//    }

    @Test
    public void getSize() throws Exception {
        assertTrue("DataContainer returns wrong size!",containerSize == paramsContainerRandom.getSize());
    }

    @Test
    public void getRequestType() throws Exception {
        assertTrue("DataContainer returns wrong RequestType!",paramsContainerRandom.getRequestType() == RequestType.RANDOM);
    }

}