package ru.sbt.util.HTTPDatapool.controllers.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CacheTableInfo {

    private String name;
    private int rowCount;
    private double progressDownload;
    private AddingStatus status;

}
