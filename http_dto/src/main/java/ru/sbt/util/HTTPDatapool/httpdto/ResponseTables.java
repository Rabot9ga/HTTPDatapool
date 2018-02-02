package ru.sbt.util.HTTPDatapool.httpdto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
public class ResponseTables implements Serializable {

    Map<String, Object> mapParameters;
    Status status;
    String statusMessage;
}
