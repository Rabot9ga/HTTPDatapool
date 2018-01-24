package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.sbt.util.HTTPDatapool.httpapi.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.DataContainerFactory;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.TestResult;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.Generator;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.TestContainerUtils;

import java.util.Map;

@Slf4j
public class DataContainerSequentialTest extends TestData {

    private DataContainerAPI<Map<String, Object>> container;

    @BeforeClass
    public void setUp() {
        container = DataContainerFactory.create(RequestType.SEQUENTIAL, list);
    }

    @Test
    public void addTable(){
        Assert.assertEquals(container.getSize(), list.size(), "DataContainerSequential size is " +
                container.getSize() + " but expected " + list.size());
    }

    @Test(enabled = false)
    public void getRow_SingleThrd_Visual(){
        for (int i = 0; i < containerSize * 2; i++) {
            Map<String, Object> row = container.getRow();
            log.debug("ID: {} Name: {}", row.get("id"), row.get("name"));
        }
    }

    @Test
    public void getSize() {
        DataContainerAPI container = DataContainerFactory.create(RequestType.SEQUENTIAL, list);
        Assert.assertEquals(list.size(), container.getSize(), "DataContainer returns wrong size!");
    }

    /**
     * 1) Filled container is created
     * 2) Using TestContainerUtils with specified threadCount and containerSize concurrent requests are generated.
     * 3) ConcurrentGetRow returns list of ID's which is sent to validation
     * 4) Result of validation is being verified with Asserts
     */

    @Test
    public void getRowFunctionalTest_Concurrent() {
        DataContainerAPI container = DataContainerFactory.create(RequestType.SEQUENTIAL, list);

        TestContainerUtils utils = new TestContainerUtils();
//        TestResult testResult = TestContainerUtils.gettingTest(utils.concurrentGetRow(container, threadCount, containerSize), threadCount, containerSize);

        TestResult testResult = TestContainerUtils.gettingTest(utils.concurrentGetRow(container, threadCount, containerSize), threadCount, containerSize);

        Assert.assertEquals(testResult.getNumOfSequentials(), testResult.getNumOfGets(),
                "data doesn't return Sequentially! numOfSequentials/numOfGets - " + testResult.getNumOfSequentials() + " / " + testResult.getNumOfGets() + "\n");
        if (containerSize > 1) {
            Assert.assertTrue(testResult.getNumOfRepeats() == 0, "Returned data is the same all the time!");
        }
    }

    @Test
    public void getRow() {
        Assert.assertTrue(container.getRow().get("id") != null, "getRow() returns null!");
    }

    @Test
    public void addRow() {
        DataContainerAPI container = DataContainerFactory.create(RequestType.SEQUENTIAL, list);
        Assert.assertEquals(container.getSize(), list.size(), "Container size doesn't match size of table used to create it!");

        int sizeBefore = container.getSize();
        container.addRow(Generator.fillDataMap(sizeBefore));
        Assert.assertEquals(sizeBefore + 1, container.getSize(), "Row wasn't added!");

        log.trace("addRow: size before: {} after: {}", sizeBefore, container.getSize());
    }

    @Test
    public void getRequestType() {
        Assert.assertEquals(container.getRequestType(), RequestType.SEQUENTIAL, "DataContainer returns wrong RequestType!");
    }

}