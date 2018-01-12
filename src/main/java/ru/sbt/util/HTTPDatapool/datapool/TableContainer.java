package ru.sbt.util.HTTPDatapool.datapool;

import lombok.*;
import ru.sbt.util.HTTPDatapool.httpapi.ParametersTable;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class TableContainer {
    DataContainerAPI container;
    String tableName;
    Set<String> columnsName;
    String scriptName;

    public boolean isFit(String tableName, RequestType requestType, Set<String> columnsName, String scriptName) {
        return tableName.equals(this.tableName)
                && requestType == container.getRequestType()
                && this.columnsName.containsAll(columnsName)
                && this.scriptName.equals(scriptName);
    }

    public boolean isFit(ParametersTable parametersTable){
        return this.tableName.equals(parametersTable.getTableName())
                && this.scriptName.equals(parametersTable.getScriptName())
                && this.columnsName.containsAll(parametersTable.getColumnsName())
                && this.container.getRequestType() == parametersTable.getType();
    }

    public Map<String, String> getData() {
        return container.getRow();
    }

}
