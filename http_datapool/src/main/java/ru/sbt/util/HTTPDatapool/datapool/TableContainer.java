package ru.sbt.util.HTTPDatapool.datapool;

import lombok.Builder;
import lombok.Data;
import ru.sbt.util.HTTPDatapool.httpdto.ParametersTable;
import ru.sbt.util.HTTPDatapool.httpdto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@Data
@Builder
public class TableContainer<T> {
    DataContainerAPI<T> container;
    String tableName;
    Set<String> columnsName;
    String scriptName;

    public boolean isFit(String tableName, RequestType requestType, Set<String> columnsName, String scriptName) {
        return tableName.equals(this.tableName)
                && requestType == container.getRequestType()
                && this.columnsName.containsAll(columnsName)
                && this.scriptName.equals(scriptName);
    }

    public boolean isFit(ParametersTable parametersTable) {

        return this.tableName.equals(parametersTable.getTableName())
                && this.scriptName.equals(parametersTable.getScriptName())
                && this.columnsName.containsAll(parametersTable.getColumnsName())
                && this.container.getRequestType() == parametersTable.getType();
    }

    public Optional<T> getDataOrElse(Supplier<Optional<List<T>>> supplier) {
        if (container.getSize() <= 0) {
            synchronized (container) {
                if (container.getSize() <= 0) {

                    Optional<List<T>> dataFromCache = supplier.get();
                    return dataFromCache.map(this::putDataReturnRow);
                }
            }
        }
        return Optional.of(container.getRow());
    }

    private T putDataReturnRow(List<T> list){
        container.addTable(list);
        return container.getRow();
    }
}
