package ru.sbt.util.HTTPDatapool.datapool;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.sbt.util.HTTPDatapool.connectionInterface.DBConnection;
import ru.sbt.util.HTTPDatapool.httpapi.*;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.Generator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.mockito.Matchers.longThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class DatapoolTest {


    private Datapool datapool;
    private List<Map<String, String>> table;
    private HashSet<String> columns;

    @Before
    public void setUp() throws Exception {
        datapool = new Datapool();
        DBConnection dbConnection = mock(DBConnection.class);
        columns = new HashSet<>();
        columns.add("123");
        columns.add("31");
        columns.add("231");
        when(dbConnection.getDataFromCache("Script1", columns)).thenReturn(Generator.genearateDataFromDB(20));
        table = dbConnection.getDataFromCache("Script1", columns);

        datapool.dbConnection = dbConnection;

    }

    @Test
    public void getParameters() {

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

        Iterator<ResponseTables> iterator = parameters.getResponseTables().iterator();
        ResponseTables responseTables = iterator.next();

        log.debug("Datapool map:\n {}", datapool.mapContainer);

        Assert.assertNotEquals(responseTables.getToken(), null);
        Assert.assertEquals(responseTables.getStatus(), Status.SUCCESS);
        Assert.assertNotEquals(responseTables.getMapParameters(), null);



    }
}