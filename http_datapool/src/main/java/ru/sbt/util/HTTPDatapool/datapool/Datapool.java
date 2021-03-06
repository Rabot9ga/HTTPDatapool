package ru.sbt.util.HTTPDatapool.datapool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sbt.util.HTTPDatapool.connectionInterface.TablesCache;
import ru.sbt.util.HTTPDatapool.httpdto.*;
import ru.sbt.util.HTTPDatapool.paramsContainer.DataContainerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Datapool {

    @Autowired
    TablesCache tablesCache;

    ConcurrentHashMap<ParametersTable, TableContainer<Map<String, Object>>> mapContainer = new ConcurrentHashMap<>();

    public DatapoolResponse getParameters(DatapoolRequest datapoolRequest) {

        // TODO: 31.01.2018 Проверка на то что таблица существует

        Map<ParametersTable, ResponseTables> responseMap = datapoolRequest.getParametersTablesStream()
                .map(parametersTable -> Pair.of(parametersTable, Optional.ofNullable(mapContainer.get(parametersTable))))
                .map(pair -> Pair.of(pair.getLeft(), pair.getRight().orElseGet(() -> createTableContainer(pair.getLeft()))))
                .collect(Collectors.toMap(Pair::getLeft, pair -> buildResponseTable(pair.getRight())));

        return DatapoolResponse.builder().responseTables(responseMap).build();
    }

    public void clearCache(String tableName) {
        log.info("Remove keys from datapool with name: {}", tableName);
        ParametersTable parameters;
        List<ParametersTable> keyToDelete = mapContainer.keySet().stream()
                .filter(parametersTable -> parametersTable.getTableName().equals(tableName))
                .collect(Collectors.toList());
        log.info("Keys to remove count: {}", keyToDelete.size());
        log.debug("Keys to remove: {}", keyToDelete);
        keyToDelete.forEach(parametersTable -> mapContainer.remove(parametersTable));
    }

    private TableContainer<Map<String, Object>> createTableContainer(ParametersTable parametersTable) {

        TableContainer<Map<String, Object>> container = TableContainer.<Map<String, Object>>builder()
                .scriptName(parametersTable.getScriptName())
                .columnsName(parametersTable.getColumnsName())
                .container(DataContainerFactory.create(parametersTable.getType()))
                .tableName(parametersTable.getTableName())
                .build();

        TableContainer<Map<String, Object>> tableContainer = mapContainer.putIfAbsent(parametersTable, container);
        if (tableContainer == null) {
            tableContainer = mapContainer.get(parametersTable);
        }
        return tableContainer;
    }

    private ResponseTables buildResponseTable(TableContainer<Map<String, Object>> tableContainer) {
        Optional<Map<String, Object>> dataFromContainer = getDataFromContainer(tableContainer);

        return dataFromContainer
                .map(map -> ResponseTables.builder()
                        .mapParameters(map)
                        .status(Status.SUCCESS)
                        .build())

                .orElse(ResponseTables.builder()
                        .mapParameters(null)
                        .status(Status.BUSY)
                        .build());

    }

    private Optional<Map<String, Object>> getDataFromContainer(TableContainer<Map<String, Object>> tableContainer) {
        return tableContainer.getDataOrElse(
                () -> tablesCache.getDataFromCache(
                        tableContainer.getTableName(),
                        tableContainer.getColumnsName()));
    }

}
