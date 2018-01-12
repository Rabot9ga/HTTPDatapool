package ru.sbt.util.HTTPDatapool.httpapi;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ResponseTables {

    UUID token;
    Map<String, String> mapParameters;
    Status status;
    String statusMessage;
}
