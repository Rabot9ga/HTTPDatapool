package ru.sbt.util.HTTPDatapool.datapool;

import lombok.*;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.util.Set;

@Data
@Builder
public class TableContainer {
    DataContainerAPI container;
    String tableName;
    Set<String> columnsName;

    public boolean isFit(String tableName, RequestType requestType){
        if (tableName.equals(this.tableName) && requestType == container.getRequestType()){
            return true;
        }
        return false;
    }

}
