package ru.sbt.util.HTTPDatapool.controllers.dto;

import lombok.Data;

@Data
public class StatusContainer {
    private String status;
    private String percent;

    public StatusContainer(String status, String percent) {
        this.status = status;
        this.percent = percent;
    }
}
