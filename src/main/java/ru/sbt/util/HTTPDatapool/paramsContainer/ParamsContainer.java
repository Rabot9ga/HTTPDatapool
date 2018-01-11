package ru.sbt.util.HTTPDatapool.paramsContainer;

import ru.sbt.util.HTTPDatapool.paramsContainer.api.ParamsContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers.DataContainerRandom;
import ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers.DataContainerUnique;

import java.util.List;
import java.util.Map;

/**
 * Class for providing composite object with ready for consuming structured parametrization
 *
 * @author SBT-Muravev-AA
 */
public class ParamsContainer implements ParamsContainerAPI {
    private RequestType requestType;

    private ParamsContainerAPI dataContainer;

    public static ParamsContainer createRandom() {
        return new ParamsContainer(RequestType.RANDOM);
    }

    public static ParamsContainer createSequential() {
        return new ParamsContainer(RequestType.SEQUENTIAL);
    }

    public static ParamsContainer createUnique() {
        return new ParamsContainer(RequestType.UNIQUE_RANDOM);
    }

    private ParamsContainer(RequestType requestType) {
        this.requestType = requestType;

        switch (requestType) {
            case RANDOM:
                this.dataContainer = new DataContainerRandom();
                break;
            case UNIQUE_SEQUENTIAL:
                this.dataContainer = new DataContainerUnique();
                break;
            case UNIQUE_RANDOM:
                this.dataContainer = new DataContainerUnique();
        }
    }
    @Override
    public Map<String, String> getRow() {
        return dataContainer.getRow();
    }

    @Override
    public void addRow(Map<String, String> row) {
        dataContainer.addRow(row);
    }

    @Override
    public <T extends List> void addTable(T collection) {
        dataContainer.addTable(collection);
    }

    @Override
    public <T extends List> T getTable() {
        return dataContainer.getTable();
    }

    @Override
    public int getSize() {
        return dataContainer.getSize();
    }
}
