package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import lombok.extern.slf4j.Slf4j;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Slf4j
public class DataContainerSequentialTest extends TestData {

    @Test
    public void getRowFunctionalTest() throws Exception {
        TestResult testResult = TestUtils.gettingTest(paramsContainerSequential, containerSize);

        assertTrue("data doesn't return Sequentially!", testResult.getNumOfSequentials() == testResult.getNumOfGets());
        if (containerSize > 1) {
            assertTrue("Returning is the same all the time!", testResult.getNumOfRepeats() == 0);
        }
    }

    @Test
    public void getRow() throws Exception {
        int sizeBefore = paramsContainerSequential.getSize();
        paramsContainerSequential.addRow(Generator.fillDataMap(sizeBefore));
        assertTrue("data doesn't return Sequentially!", sizeBefore + 1 == paramsContainerSequential.getSize());
    }

    @Test
    public void addRow() throws Exception {

        // FIXME: 12.01.2018 it doesn't check if data is adding correctly

        DataContainerAPI container = Generator.fillDataContainerSequential(containerSizeRow);
        assertTrue(container.getSize() == containerSizeRow);
    }

    @Test
    public void getTable() throws Exception {
        assertTrue("DataContainerSequential size is incorrect! " + paramsContainerSequential.getTable().size() +
                " not equals " + containerSize, paramsContainerSequential.getTable().size() == containerSize);
        assertTrue("ID of first element in Table is not 0!", paramsContainerSequential.getTable().get(0).get("id").equals("0"));
    }

    @Test
    public void addTable() throws Exception {
        assertFalse("DataContainerSequential size is 0!!", paramsContainerSequential.getSize() == 0);
        assertFalse("DataContainerSequential is empty!", paramsContainerSequential.getTable().isEmpty());
        assertTrue("Added List size is not equal to Container Table size!", list.size() == paramsContainerSequential.getTable().size());
        assertTrue("DataContainerSequential size is incorrect! " + paramsContainerSequential.getTable().size() +
                " not equals " + containerSize, paramsContainerSequential.getTable().size() == containerSize);
    }


    @Test
    public void getSize() throws Exception {
        assertTrue("DataContainer returns wrong size!", containerSize == paramsContainerSequential.getSize());
    }

    @Test
    public void getRequestType() throws Exception {
        assertTrue("DataContainer returns wrong RequestType!", paramsContainerSequential.getRequestType() == RequestType.SEQUENTIAL);
    }

}