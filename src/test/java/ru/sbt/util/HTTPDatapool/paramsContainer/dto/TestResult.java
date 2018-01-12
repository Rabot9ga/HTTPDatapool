package ru.sbt.util.HTTPDatapool.paramsContainer.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestResult {
    private int numOfSequentials;
    private int numOfRepeats;
    private int numOfGets;
}
