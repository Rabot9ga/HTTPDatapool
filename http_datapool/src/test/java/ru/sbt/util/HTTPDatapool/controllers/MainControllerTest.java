package ru.sbt.util.HTTPDatapool.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
import java.util.stream.Collectors;

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

        List<Map<String, Object>> dataFromCache = dbConnection.getDataFromCache(tableName, columns);

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

        List<Map<String, String>> dataFromCacheConvert = dataFromCache.stream()
                .map(this::convertMap)
                .collect(Collectors.toList());



        datapoolResponse.getResponseTables().forEach((parametersTable1, responseTables) -> Assert.assertTrue(dataFromCacheConvert.contains(convertMap(responseTables.getMapParameters()))));


        log.info("=======================================================");
        log.info("=======================================================");

    }

    private Map<String, String> convertMap(Map<String, Object> map) {
        return map.entrySet().stream()
                .map(entry -> ImmutablePair.of(entry.getKey(), entry.getValue().toString()))
                .collect(Collectors.toMap(o -> o.left, o -> o.right));
    }
}