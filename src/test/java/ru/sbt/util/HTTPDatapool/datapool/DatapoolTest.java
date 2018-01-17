package ru.sbt.util.HTTPDatapool.datapool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.sbt.util.HTTPDatapool.connectionInterface.DBConnection;
import ru.sbt.util.HTTPDatapool.httpapi.*;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.Generator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.when;

@Slf4j
public class DatapoolTest {


    private Datapool datapool;
    private HashSet<String> columns;
    private DBConnection dbConnection;

    private Map<String, List<Map<String, String>>> tableMap;

    @BeforeTest
    public void setUp() throws Exception {

        dbConnection = Mockito.mock(DBConnection.class);
        columns = new HashSet<>();
        columns.add("123");
        columns.add("31");
        columns.add("231");

        tableMap = IntStream.range(1, 11)
                .mapToObj(value -> "Script" + value)
                .map(value -> ImmutablePair.of(value, Generator.fillDataList(2)))
                .collect(Collectors.toMap(o -> o.left, o -> o.right));


        tableMap.forEach((key, value) -> when(dbConnection.getDataFromCache(key, columns)).thenReturn(tableMap.get(key)));

        datapool = new Datapool();
        datapool.dbConnection = dbConnection;
    }

    @DataProvider(name = "concurrentGetParameters", parallel = true)
    public Object[][] concurrentGetParameters() {

        final int parametersCount = 10;

        List<ParametersTable> paramsTable = IntStream.rangeClosed(1, parametersCount)
                .mapToObj(value -> ParametersTable.builder()
                        .columnsName(columns)
                        .tableName("Script" + value)
                        .scriptName("Script" + value)
                        .type(RequestType.RANDOM).build())
                .collect(Collectors.toList());

        List<List<Map<String, String>>> parametersFromDB = IntStream.rangeClosed(1, 10)
                .mapToObj(value -> tableMap.get("Script" + value))
                .collect(Collectors.toList());

        Object[][] output = new Object[paramsTable.size()][2];

        for (int i = 0; i < paramsTable.size(); i++) {
            output[i][0] = paramsTable.get(i);
            output[i][1] = parametersFromDB.get(i);
        }

        return output;
    }

    @Test(invocationCount = 100, threadPoolSize = 2, dataProvider = "concurrentGetParameters")
    public void testConcurrentGetParameters(ParametersTable parametersTable, List<Map<String, String>> parametersFromDB) throws Exception {
        testGet(parametersTable, parametersFromDB);

    }

    @Test(invocationCount = 100, dataProvider = "concurrentGetParameters", singleThreaded = true)
    public void testGetParameters(ParametersTable parametersTable, List<Map<String, String>> parametersFromDB) throws Exception {
        testGet(parametersTable, parametersFromDB);

    }

    private void testGet(ParametersTable parametersTable, List<Map<String, String>> parametersFromDB) {
        log.debug("ParametersTable: {}", parametersTable);
        log.debug("ParametersFromDB: {}", parametersFromDB);

        HashSet<ParametersTable> parametersTables = new HashSet<>();
        parametersTables.add(parametersTable);
        HTTPRequestParam requestParam = HTTPRequestParam.builder().parametersTables(parametersTables).build();
        HTTPResponseParam parameters = null;
//        synchronized (datapool){
        parameters = datapool.getParameters(requestParam);
//        }

        parameters.getResponseTables().forEach((parametersTable1, responseTables) -> assertResponse(responseTables, parametersFromDB));
    }

    @AfterTest
    public void checkSizeHashMap() {
        log.debug("datapool.mapContainer.mappingCount() = {}", datapool.mapContainer.mappingCount());
        log.debug("datapool.mapContainer: {}", datapool.mapContainer);
        Assert.assertEquals(datapool.mapContainer.mappingCount(), 10);
    }


    private void assertResponse(ResponseTables responseTables, List<Map<String, String>> tableData) {

        Assert.assertEquals(responseTables.getStatus(), Status.SUCCESS);
        Assert.assertTrue(tableData.contains(responseTables.getMapParameters()));
    }


}