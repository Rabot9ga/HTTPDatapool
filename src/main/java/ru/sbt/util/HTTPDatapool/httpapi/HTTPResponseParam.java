package ru.sbt.util.HTTPDatapool.httpapi;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.stream.Stream;

@Data
@Builder
public class HTTPResponseParam {
    Set<ResponseTables> responseTables;

    public Stream<ResponseTables> getResponseTablesStream(){
        return responseTables.stream();
    }
}
