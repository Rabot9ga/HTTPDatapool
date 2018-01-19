package ru.sbt.util.HTTPDatapool.httpapi;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Stream;

@Data
@Builder
public class DatapoolRequest implements Serializable {
    Set<ParametersTable> parametersTables;

    public Stream<ParametersTable> getParametersTablesStream() {
        return parametersTables.stream();
    }
}
