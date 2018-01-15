package ru.sbt.util.HTTPDatapool.datapool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sbt.util.HTTPDatapool.connectionInterface.DBConnection;
import ru.sbt.util.HTTPDatapool.httpapi.*;
import ru.sbt.util.HTTPDatapool.paramsContainer.DataContainerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class Datapool {

    @Autowired
    DBConnection dbConnection;

    Map<UUID, TableContainer> mapContainer = new ConcurrentHashMap<>();

    public HTTPResponseParam getParameters(HTTPRequestParam requestParam) {

        Map<UUID, ParametersTable> tokens = findTokens(requestParam);

        Set<ResponseTables> responses = tokens.entrySet().stream()
                .map(this::createResponseTables)
                .collect(Collectors.toSet());

        return HTTPResponseParam.builder().responseTables(responses).build();
    }


    private ResponseTables createResponseTables(Map.Entry<UUID, ParametersTable> entry) {
        Map<String, String> data = mapContainer.get(entry.getKey()).getData();

        return ResponseTables.builder()
                .token(entry.getKey())
                .mapParameters(data)
                .status(Status.SUCCESS)
                .build();
    }

    private Map<UUID, ParametersTable> findTokens(HTTPRequestParam requestParam) {
        return requestParam.getParametersTablesStream()
                .collect(Collectors.toMap(this::findOrCreateToken, o -> o));

    }

    private UUID findOrCreateToken(ParametersTable parametersTable) {
        // TODO: 12.01.2018 Че-нить придумать, чтобы не делать 2 раза гет из мапы
        UUID token = parametersTable.getToken();
        if (token != null) {
            if (mapContainer.containsKey(token)) {
                TableContainer tableContainer = mapContainer.get(token);
                if (tableContainer.isFit(parametersTable)) return token;
            }
        }

        return streamFromMapParameters()
                .filter(uuidTableContainerEntry -> uuidTableContainerEntry.getValue().isFit(parametersTable))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseGet(() -> createToken(parametersTable));

    }

    private UUID createToken(ParametersTable parametersTable) {
        List<Map<String, String>> dataFromCache = dbConnection.getDataFromCache(parametersTable.getTableName(), parametersTable.getColumnsName(), false);

        TableContainer tableContainer = TableContainer.builder()
                .tableName(parametersTable.getTableName())
                .columnsName(parametersTable.getColumnsName())
                .scriptName(parametersTable.getScriptName())
                .container(DataContainerFactory.create(parametersTable.getType(), dataFromCache))
                .build();

        UUID uuid = UUID.randomUUID();
        mapContainer.put(uuid, tableContainer);
        return uuid;
    }


    private Stream<Map.Entry<UUID, TableContainer>> streamFromMapParameters() {
        return mapContainer.entrySet().stream();
    }


}
