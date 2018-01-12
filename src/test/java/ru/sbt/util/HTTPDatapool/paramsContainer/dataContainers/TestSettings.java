package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

public abstract class TestSettings {
    /**
     * Size of container
     */
    protected int containerSize = 5_000_000;
    /**
     * Size of container for test addRow
     */
    protected int containerSizeRow = 1_000;
}
