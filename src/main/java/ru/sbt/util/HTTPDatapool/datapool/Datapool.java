package ru.sbt.util.HTTPDatapool.datapool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sbt.util.HTTPDatapool.connectionInterface.DBConnection;
import ru.sbt.util.HTTPDatapool.httpapi.HTTPRequestParam;
import ru.sbt.util.HTTPDatapool.httpapi.ParametersTable;
import ru.sbt.util.HTTPDatapool.httpapi.ResponseParam;
import ru.sbt.util.HTTPDatapool.httpapi.Status;
import ru.sbt.util.HTTPDatapool.paramsContainer.DataContainerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class Datapool {

    @Autowired
    DBConnection dbConnection;

    // FIXME: 11.01.2018 тут будет не сет object'ов.
    Map<UUID, TableContainer> mapContainer = new ConcurrentHashMap<>();

    public ResponseParam getParameters(HTTPRequestParam requestParam) {

        if (requestParam.getParametersTablesStream()
                .anyMatch(parametersTable -> parametersTable.getToken() == null)) {
            Map<UUID, ParametersTable> tokens = findTokens(requestParam);
        }


        return ResponseParam.builder()
//                .token(uuid)
                .status(Status.UPDATED)
                // FIXME: 11.01.2018 передать параметры
                .mapParameters(new HashMap<>())
                .build();
    }


    private Map<UUID, ParametersTable> findTokens(HTTPRequestParam requestParam) {
        return requestParam.getParametersTablesStream()
                .collect(Collectors.toMap(this::findOrCreateToken, o -> o));

    }

    private UUID findOrCreateToken(ParametersTable parametersTable) {
        return streamFromMapParameters()
                .filter(uuidTableContainerEntry -> uuidTableContainerEntry.getValue().isFit(parametersTable.getTableName(), parametersTable.getType()))
                .map(Map.Entry::getKey)
                .findAny()
                .orElse(createToken(parametersTable));
    }

    private UUID createToken(ParametersTable parametersTable) {
        TableContainer tableContainer = TableContainer.builder()
                .tableName(parametersTable.getTableName())
                .container(DataContainerFactory.create(parametersTable.getType()))
                .build();

        UUID uuid = UUID.randomUUID();
        mapContainer.put(uuid, tableContainer);
        return uuid;
    }


    private Stream<Map.Entry<UUID, TableContainer>> streamFromMapParameters() {
        return mapContainer.entrySet().stream();
    }



}
