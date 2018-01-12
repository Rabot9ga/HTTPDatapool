package ru.sbt.util.HTTPDatapool.datapool;

import lombok.Builder;
import lombok.Data;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

@Builder
@Data
public class TableContainer {
    DataContainerAPI container;
    String tableName;

    public boolean isFit(String tableName, RequestType requestType){
        if (tableName.equals(this.tableName) && requestType == container.getRequestType()){
            return true;
        }
        return false;
    }

}
