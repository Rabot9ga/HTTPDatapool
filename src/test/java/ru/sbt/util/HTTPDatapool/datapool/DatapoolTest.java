package ru.sbt.util.HTTPDatapool.datapool;

import lombok.extern.slf4j.Slf4j;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.sbt.util.HTTPDatapool.connectionInterface.DBConnection;
import ru.sbt.util.HTTPDatapool.httpapi.*;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.Generator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class DatapoolTest {


    private Datapool datapool;
    private HashSet<String> columns;
    private DBConnection dbConnection;

    @BeforeClass
    public void setUp() throws Exception {
        datapool = new Datapool();
        dbConnection = mock(DBConnection.class);
        columns = new HashSet<>();
        columns.add("123");
        columns.add("31");
        columns.add("231");


        datapool.dbConnection = dbConnection;

        when(dbConnection.getDataFromCache("Script1", columns, false)).thenReturn(Generator.genearateDataFromDB(20));
        when(dbConnection.getDataFromCache("Script1", columns, true)).thenReturn(Generator.genearateDataFromDB(20));

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


    @Test
    public void testConcurrentGetParameters() throws Exception {

    }

    private void assertResponse(ResponseTables responseTables, List<Map<String, String>> tableData) {
        Assert.assertNotEquals(responseTables.getToken(), null);
        Assert.assertEquals(responseTables.getStatus(), Status.SUCCESS);
        Assert.assertTrue(tableData.contains(responseTables.getMapParameters()));
    }


}