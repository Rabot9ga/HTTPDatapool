package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.sbt.util.HTTPDatapool.httpdto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.DataContainerFactory;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.TestResult;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.Generator;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.TestContainerUtils;

import java.util.Map;

@Slf4j
public class DataContainerRandomTest extends TestData {

    private DataContainerAPI<Map<String, Object>> container;

    @BeforeClass
    public void setUp() throws Exception {
        container = DataContainerFactory.create(RequestType.RANDOM, list);
    }

    @Test
    public void addTable() throws Exception {
        Assert.assertEquals(container.getSize(), list.size(), "DataContainerSequential size is " +
                container.getSize() + " but expected " + list.size());
    }

    @Test(enabled = false)
    public void getRow_SingleThrd_Visual() throws Exception {
        DataContainerAPI<Map<String, Object>> container = DataContainerFactory.create(RequestType.RANDOM, list);

        for (int i = 0; i < containerSize; i++) {
            Map<String, Object> row = container.getRow();
            log.debug("ID: {} Name: {}", row.get("id"), row.get("name"));
        }
    }

    @Test
    public void getRowFunctionalTest_Concurrent() {
        DataContainerAPI<Map<String, Object>> container = DataContainerFactory.create(RequestType.RANDOM, list);

        // sending returned list of id's for validation

        TestContainerUtils utils = new TestContainerUtils();
        TestResult testResult = TestContainerUtils.gettingTest(utils.concurrentGetRow(container, threadCount, containerSize), threadCount, containerSize);

        Assert.assertTrue(testResult.getNumOfSequentials() < testResult.getNumOfGets() * 0.5,
                "Data returns sequentially in more than 50% of requests!");
        if (containerSize > 10) {
            Assert.assertTrue(testResult.getNumOfRepeats() < testResult.getNumOfGets() * 0.5, "Returned data is the same in >= 50% of requests");
        }
    }

    @Test()
    public void getSize() throws Exception {
        DataContainerAPI container = DataContainerFactory.create(RequestType.RANDOM, list);
        Assert.assertEquals(list.size(), container.getSize(), "DataContainer returns wrong size!");
    }

//    @Test(priority = 1)
//    public void getRowFunctionalTest() throws Exception {
//        TestResult testResult = TestContainerUtils.gettingTest(container, containerSize);
//
//        Assert.assertTrue(testResult.getNumOfSequentials() < testResult.getNumOfGets(), "data doesn't return Sequentially!");
//        if (containerSize > 1) {
//            Assert.assertTrue(testResult.getNumOfRepeats() < testResult.getNumOfGets(), "Returning is the same all the time!");
//        }
//    }

    @Test
    public void getRow() throws Exception {
        Assert.assertTrue(container.getRow().get("id") != null, "getRow() returns null!");
    }

    @Test
    public void addRow() throws Exception {
        DataContainerAPI container = DataContainerFactory.create(RequestType.RANDOM, list);
        Assert.assertEquals(container.getSize(), list.size(), "Container size doesn't match size of table used to create it!");

        int sizeBefore = container.getSize();
        container.addRow(Generator.fillDataMap(sizeBefore));
        Assert.assertTrue(sizeBefore + 1 == container.getSize(), "Row wasn't added!");

        log.trace("addRow: size before: {} after: {}", sizeBefore, container.getSize());
    }

    @Test
    public void getRequestType() throws Exception {
        Assert.assertTrue(container.getRequestType() == RequestType.RANDOM, "DataContainer returns wrong RequestType!");
    }

}