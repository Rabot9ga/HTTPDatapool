package ru.sbt.util.HTTPDatapool.httpapi;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.stream.Stream;

@Data
@Builder
public class HTTPRequestParam {
    Set<ParametersTable> parametersTables;

    public Stream<ParametersTable> getParametersTablesStream() {
        return parametersTables.stream();
    }
}
