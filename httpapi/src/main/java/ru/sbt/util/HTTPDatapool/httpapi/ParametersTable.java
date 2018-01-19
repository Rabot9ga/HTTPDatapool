package ru.sbt.util.HTTPDatapool.httpapi;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Stream;

@Data
@Builder
public class ParametersTable implements Serializable {

    RequestType type;
    String tableName;
    Set<String> columnsName;
    String scriptName;

    public Stream<String> getColumnsNameStream(){
        return columnsName.stream();
    }

}
