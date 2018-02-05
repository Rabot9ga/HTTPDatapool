package ru.sbt.util.HTTPDatapool.controllers;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;
import ru.sbt.util.HTTPDatapool.connectionInterface.DBRepository;
import ru.sbt.util.HTTPDatapool.httpdto.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.testng.Assert.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "logging.level.ru.sbt.util.HTTPDatapool=debug")
@Slf4j
public class MainControllerTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    TestRestTemplate testRestTemplate;

    @LocalServerPort
    int port;

    private MainController mainController;

    @Autowired
    private DBRepository dbRepository;

    @Test(threadPoolSize = 10, invocationCount = 20)
    public void getParameter() throws IOException, InterruptedException {


        HashSet<String> columns = new HashSet<>();
        columns.add("COLUMN1");
        columns.add("COLUMN2");
        columns.add("COLUMN3");
        columns.add("COLUMN4");

        String tableName = "FORTEST2";

        int tableSize = dbRepository.getTableSize(tableName);
        List<Map<String, Object>> dataFromTable = dbRepository.getFromTableBetween(tableName, 1, tableSize);

        ParametersTable parametersTable = ParametersTable.builder()
                .scriptName("FORTEST2")
                .tableName(tableName)
                .type(RequestType.RANDOM)
                .columnsName(columns)
                .build();

        Set<ParametersTable> parametersTables = new HashSet<>();
        parametersTables.add(parametersTable);

        DatapoolRequest request = DatapoolRequest.builder().parametersTables(parametersTables).build();
//
        log.info("=======================================================");
        log.info("=======================================================");
        log.info("Columns Name: {}", columns);
        log.info("TableName: {}", tableName);
        log.info("DataFromTable: {}", dataFromTable);
        log.info("DatapoolRequest: {}", request);


        ResponseEntity<DatapoolResponse> response;

        while (true) {
            response = testRestTemplate.postForEntity("/api/getParameter", request, DatapoolResponse.class);

            log.debug("response: {}", response);

            int code = response.getStatusCodeValue();
            log.info("response.code() = {}", code);

            assertEquals(response.getStatusCodeValue(), 200, "status code is not 200");

            DatapoolResponse datapoolResponse = response.getBody();
            log.info("DatapoolResponse: {}", datapoolResponse);

            Map<ParametersTable, ResponseTables> tables = datapoolResponse.getResponseTables();
            tables.forEach((parametersTable1, responseTables) -> assertNotEquals(responseTables.getStatus(), Status.ERROR, "Status ERROR"));

            if (tables.entrySet().stream().allMatch(entry -> !entry.getValue().getStatus().equals(Status.BUSY))) {
                break;
            }
            TimeUnit.SECONDS.sleep(1);
        }

        List<Map<String, String>> convetedData = dataFromTable.stream().map(this::convertMap).collect(Collectors.toList());

        assertTrue(response.getBody().getResponseTablesStream()
                .map(entry -> entry.getValue().getMapParameters())
                .allMatch(map -> convetedData.contains(convertMap(map))), "Data from response does'n match data from DB");

    }

    private Map<String, String> convertMap(Map<String, Object> map) {
        return map.entrySet().stream()
                .map(entry -> ImmutablePair.of(entry.getKey(), entry.getValue().toString()))
                .collect(Collectors.toMap(o -> o.left, o -> o.right));
    }
}