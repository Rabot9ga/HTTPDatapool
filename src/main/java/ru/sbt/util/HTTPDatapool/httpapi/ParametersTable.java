package ru.sbt.util.HTTPDatapool.httpapi;

import lombok.Builder;
import lombok.Data;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Data
@Builder
public class ParametersTable {

    UUID token;
    RequestType type;
    String tableName;
    Set<String> columnsName;

    public Stream<String> getColumnsNameStream(){
        return columnsName.stream();
    }
}
