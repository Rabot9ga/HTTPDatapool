package ru.sbt.util.HTTPDatapool.httpdto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Stream;

@Data
@Builder
public class DatapoolResponse implements Serializable {

    @JsonDeserialize(keyUsing = ParametersTableDeserializer.class)
    private Map<ParametersTable, ResponseTables> responseTables;

    @JsonIgnore
    public Stream<Map.Entry<ParametersTable, ResponseTables>> getResponseTablesStream() {
        return responseTables.entrySet().stream();
    }
}
