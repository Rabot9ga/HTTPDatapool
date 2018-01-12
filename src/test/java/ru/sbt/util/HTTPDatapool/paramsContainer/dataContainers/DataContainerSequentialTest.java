package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.TestResult;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.Generator;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.TestUtils;

import static org.junit.Assert.assertTrue;

@Slf4j
public class DataContainerSequentialTest {
    private int containerSize = 15;
    DataContainerAPI paramsContainerSequential = Generator.fillDataContainerSequential(containerSize);

    @Test
    public void getRow() throws Exception {
        TestResult testResult = TestUtils.gettingTest(paramsContainerSequential, containerSize);
        for (int i = 0; i < 30; i++) {
            log.debug(paramsContainerSequential.getRow().get("id"));
        }

        assertTrue("data doesn't return Sequentially!", testResult.getNumOfSequentials() == testResult.getNumOfGets());
        if (containerSize > 1) {
            assertTrue("Returning is the same all the time!", testResult.getNumOfRepeats() == 0);
        }

    }

    @Test
    public void addRow() throws Exception {

        // FIXME: 12.01.2018 it doesn't check if data is adding correctly

        DataContainerAPI container = Generator.fillDataContainerSequential(containerSize);
        assertTrue(container.getSize() == container.getSize());
    }

    @Test
    public void getTable() throws Exception {
    }

    @Test
    public void addTable() throws Exception {
    }

    @Test
    public void getSize() throws Exception {
        assertTrue("DataContainer returns wrong size!",containerSize == paramsContainerSequential.getSize());
    }

    @Test
    public void getRequestType() throws Exception {
        assertTrue("DataContainer returns wrong RequestType!",paramsContainerSequential.getRequestType() == RequestType.SEQUENTIAL);
    }

}