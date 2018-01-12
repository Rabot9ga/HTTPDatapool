package ru.sbt.util.HTTPDatapool.paramsContainer.dto;

public class TestResult {
    private int numOfSequentials;
    private int numOfRepeats;
    private int numOfGetts;

    public TestResult withNumOfSequentials(int numOfSequentials){
        this.numOfSequentials = numOfSequentials;
        return this;
    }

    public TestResult withNumOfRepeats(int numOfRepeats){
        this.numOfRepeats = numOfRepeats;
        return this;
    }

    public TestResult withNumOfGetts(int numOfGetts){
        this.numOfGetts = numOfGetts;
        return this;
    }

    public int getNumOfGetts() {
        return numOfGetts;
    }

    public int getNumOfSequentials() {
        return numOfSequentials;
    }

    public void setNumOfSequentials(int numOfSequentials) {
        this.numOfSequentials = numOfSequentials;
    }

    public int getNumOfRepeats() {
        return numOfRepeats;
    }

    public void setNumOfRepeats(int numOfRepeats) {
        this.numOfRepeats = numOfRepeats;
    }
}
