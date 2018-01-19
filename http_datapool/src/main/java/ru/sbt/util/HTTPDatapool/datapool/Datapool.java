package ru.sbt.util.HTTPDatapool.datapool;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sbt.util.HTTPDatapool.connectionInterface.DBConnection;
import ru.sbt.util.HTTPDatapool.httpapi.*;
import ru.sbt.util.HTTPDatapool.paramsContainer.DataContainerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class Datapool {

    @Autowired
    DBConnection dbConnection;

    ConcurrentHashMap<ParametersTable, TableContainer> mapContainer = new ConcurrentHashMap<>();

    public DatapoolResponse getParameters(DatapoolRequest datapoolRequest) {

        Map<ParametersTable, ResponseTables> responseMap = datapoolRequest.getParametersTablesStream()
                .map(parametersTable -> Pair.of(parametersTable, Optional.ofNullable(mapContainer.get(parametersTable))))
                .map(pair -> Pair.of(pair.getLeft(), pair.getRight().orElseGet(() -> createTableContainer(pair.getLeft()))))
                .collect(Collectors.toMap(Pair::getLeft, pair -> buildResponseTable(pair.getRight())));

        return DatapoolResponse.builder().responseTables(responseMap).build();
    }

    public void clearCache(){

        mapContainer.clear();
        dbConnection.clearCache();
    }

    private ResponseTables buildResponseTable(TableContainer tableContainer) {
        return ResponseTables.builder()
                .mapParameters(getDataFromContainer(tableContainer))
                .status(Status.SUCCESS)
                .build();
    }

    private Map<String, String> getDataFromContainer(TableContainer tableContainer) {
        return tableContainer.getDataOrElse(
                () -> dbConnection.getDataFromCache(
                        tableContainer.getTableName(),
                        tableContainer.getColumnsName()));
    }

    private TableContainer createTableContainer(ParametersTable parametersTable) {
        TableContainer container = TableContainer.builder()
                .scriptName(parametersTable.getScriptName())
                .columnsName(parametersTable.getColumnsName())
                .container(DataContainerFactory.create(parametersTable.getType()))
                .tableName(parametersTable.getTableName())
                .build();

        TableContainer tableContainer = mapContainer.putIfAbsent(parametersTable, container);
        if (tableContainer == null) {
            tableContainer = mapContainer.get(parametersTable);
        }
        return tableContainer;
    }

}
