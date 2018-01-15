package ru.sbt.util.HTTPDatapool.datapool;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.assertj.core.groups.Tuple;
import org.testng.Assert;
import org.testng.annotations.*;
import ru.sbt.util.HTTPDatapool.connectionInterface.DBConnection;
import ru.sbt.util.HTTPDatapool.httpapi.*;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.Generator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class DatapoolTest {


    private Datapool datapool;
    private HashSet<String> columns;
    private DBConnection dbConnection;

    private Map<String, List<Map<String, String>>> tableMap;

    @BeforeClass
    public void setUpClass() throws Exception {

        dbConnection = mock(DBConnection.class);
        columns = new HashSet<>();
        columns.add("123");
        columns.add("31");
        columns.add("231");

        tableMap = IntStream.range(1, 11)
                .mapToObj(value -> "Script" + value)
                .map(value -> ImmutablePair.of(value, Generator.genearateDataFromDB(20)))
                .collect(Collectors.toMap(o -> o.left, o -> o.right));


        tableMap.forEach((key, value) -> when(dbConnection.getDataFromCache(key, columns, false)).thenReturn(tableMap.get(key)));
        tableMap.forEach((key, value) -> when(dbConnection.getDataFromCache(key, columns, true)).thenReturn(tableMap.get(key)));

    }

    @BeforeMethod
    public void setUp() throws Exception {
        datapool = new Datapool();
        datapool.dbConnection = dbConnection;
    }

    @Test(invocationCount = 10)
    public void getParameters_1script() {
        List<Map<String, String>> tableData = dbConnection.getDataFromCache("Script1", columns, false);

        log.debug("Test table data: \n{}", tableData);
        HashSet<ParametersTable> parametersTables = new HashSet<>();

        ParametersTable table = ParametersTable.builder()
                .columnsName(columns)
                .tableName("Script1")
                .scriptName("Script1")
                .type(RequestType.RANDOM).build();

        parametersTables.add(table);


        HTTPRequestParam requestParam = HTTPRequestParam.builder().parametersTables(parametersTables).build();
        HTTPResponseParam parameters = datapool.getParameters(requestParam);


        log.debug("ResponseTables:\n {}", parameters);
        log.debug("Datapool map:\n {}", datapool.mapContainer);
        parameters.getResponseTablesStream().forEach(responseTables -> assertResponse(responseTables, tableData));

        Assert.assertEquals(datapool.mapContainer.size(), 1);
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

    @Test(invocationCount = 100, threadPoolSize = 10, dataProvider = "concurrentGetParameters")
    public void testConcurrentGetParameters(ParametersTable parametersTable, List<Map<String, String>> parametersFromDB) throws Exception {
        HashSet<ParametersTable> parametersTables = new HashSet<>();
        parametersTables.add(parametersTable);
        HTTPRequestParam requestParam = HTTPRequestParam.builder().parametersTables(parametersTables).build();
        HTTPResponseParam parameters = datapool.getParameters(requestParam);

        parameters.getResponseTablesStream().forEach(responseTables -> assertResponse(responseTables, parametersFromDB));

    }

    @AfterTest
    public void checkSizeHashMap(){
        log.debug("datapool.mapContainer.size() = {}", datapool.mapContainer.size());
        Assert.assertTrue(datapool.mapContainer.size() <= 10);
    }


    private void assertResponse(ResponseTables responseTables, List<Map<String, String>> tableData) {
        Assert.assertNotEquals(responseTables.getToken(), null);
        Assert.assertEquals(responseTables.getStatus(), Status.SUCCESS);
        Assert.assertTrue(tableData.contains(responseTables.getMapParameters()));
    }


}