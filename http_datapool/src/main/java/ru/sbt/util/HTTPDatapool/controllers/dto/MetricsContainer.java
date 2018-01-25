package ru.sbt.util.HTTPDatapool.controllers.dto;

import lombok.Data;

@Data
public class MetricsContainer {
    private String name;
    private String value;
    private String info;

    public MetricsContainer(String name, String value, String info) {
        this.name = name;
        this.value = value;
        this.info = info;
    }
}
