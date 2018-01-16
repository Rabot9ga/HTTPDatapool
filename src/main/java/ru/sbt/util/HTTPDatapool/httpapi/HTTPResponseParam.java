package ru.sbt.util.HTTPDatapool.httpapi;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class HTTPResponseParam {
    Map<ParametersTable, ResponseTables> responseTables;

}
