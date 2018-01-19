package ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers;

public abstract class TestSettings {
    /**
     * Size of container
     */
    protected int containerSize = 10000;
    /**
     * Size of container for test addRow
     */
    protected int containerSizeRow = 10;

    /**
     * Number of threads used for executing getRow() in concurrent tests
     */
    protected int threadCount = 10;
}
