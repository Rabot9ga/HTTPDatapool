package ru.sbt.util.HTTPDatapool.paramsContainer;

import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers.DataContainerSequential;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers.DataContainerRandom;
import ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers.DataContainerUnique;

import java.lang.instrument.IllegalClassFormatException;

/**
 * Class for providing composite object with ready for consuming structured parametrization
 *
 * @author SBT-Muravev-AA
 */
public class DataContainerFactory {

    public static DataContainerAPI createRandom() {
        return new DataContainerRandom();
    }

    public static DataContainerAPI createSequential() {
        return new DataContainerSequential();
    }

    public static DataContainerAPI createUnique(RequestType type) {
        return new DataContainerUnique(type);
    }

    public static DataContainerAPI create(RequestType type) {
        DataContainerAPI dataContainer;

        switch (type) {
            case RANDOM:
                dataContainer = new DataContainerRandom();
                break;
            case SEQUENTIAL:
                dataContainer = new DataContainerSequential();
                break;
            case UNIQUE_SEQUENTIAL:
                dataContainer = new DataContainerUnique(type);
                break;
            case UNIQUE_RANDOM:
                dataContainer = new DataContainerUnique(type);
                break;

            default:
                throw new RuntimeException("Can't create Data Container: Wrong RequestType format!");

        }
        return dataContainer;
    }
}
