package ru.sbt.util.HTTPDatapool.httpapi;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ResponseTables {

    //    UUID token;
    Map<String, String> mapParameters;
    Status status;
    String statusMessage;
}
