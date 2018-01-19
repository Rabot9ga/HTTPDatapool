package ru.sbt.util.HTTPDatapool.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.sbt.util.HTTPDatapool.connectionInterface.DBConnection;
import ru.sbt.util.HTTPDatapool.controllers.Interfaces.MainController;
import ru.sbt.util.HTTPDatapool.httpapi.DatapoolRequest;
import ru.sbt.util.HTTPDatapool.httpapi.DatapoolResponse;
import ru.sbt.util.HTTPDatapool.httpapi.ParametersTable;
import ru.sbt.util.HTTPDatapool.httpapi.RequestType;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
public class MainControllerTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    TestRestTemplate testRestTemplate;

    @LocalServerPort
    int port;

    private MainController mainController;

    @Autowired
    private DBConnection dbConnection;

    @BeforeClass
    public void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:" + port)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        mainController = retrofit.create(MainController.class);
    }

    @Test
    public void getParameter() throws IOException {


        HashSet<String> columns = new HashSet<>();
        columns.add("ID");
        columns.add("NAME");
        columns.add("AGE");
        columns.add("PHONE");

        String tableName = "FORTEST";

        List<Map<String, String>> dataFromCache = dbConnection.getDataFromCache(tableName, columns);

        ParametersTable parametersTable = ParametersTable.builder()
                .scriptName("FORTEST")
                .tableName(tableName)
                .type(RequestType.RANDOM)
                .columnsName(columns)
                .build();

        HashSet<ParametersTable> parametersTables = new HashSet<>();
        parametersTables.add(parametersTable);

        DatapoolRequest request = DatapoolRequest.builder().parametersTables(parametersTables).build();
//
        log.info("=======================================================");
        log.info("=======================================================");
        log.info("Columns Name: {}", columns);
        log.info("TableName: {}", tableName);
        log.info("DataFromCache: {}", dataFromCache);
        log.info("DatapoolRequest: {}", request);

        Response<DatapoolResponse> response = mainController.getParameter(request).execute();

        DatapoolResponse datapoolResponse = response.body();
        log.info("DatapoolResponse: {}", datapoolResponse);

        datapoolResponse.getResponseTables().forEach((parametersTable1, responseTables) -> Assert.assertTrue(dataFromCache.contains(responseTables.getMapParameters())));
        log.info("=======================================================");
        log.info("=======================================================");

    }
}