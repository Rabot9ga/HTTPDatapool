package ru.sbt.util.HTTPDatapool.controllers.dto;

import lombok.Data;

@Data
public class MetricsContainer {
    private String category;
    private String name;
    private String value;
    private String info;

    public MetricsContainer(String category, String name, String value, String info) {
        this.category = category;
        this.name = name;
        this.value = value;
        this.info = info;
    }
}
