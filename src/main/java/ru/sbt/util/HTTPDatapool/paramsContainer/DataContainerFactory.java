package ru.sbt.util.HTTPDatapool.paramsContainer;

import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers.DataContainerSequential;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers.DataContainerRandom;
import ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers.DataContainerUnique;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Collection;

/**
 * Class for providing composite object with ready for consuming structured parametrization
 *
 * @author SBT-Muravev-AA
 */
public class DataContainerFactory {

    public static DataContainerAPI create(RequestType type) {
        return createContainer(type, null);
    }

    public static DataContainerAPI create(RequestType type, Collection collection) {
        return createContainer(type, collection);
    }
    private static DataContainerAPI createContainer(RequestType type, Collection collection){
        DataContainerAPI dataContainer;

        switch (type) {
            case RANDOM:
                dataContainer = new DataContainerRandom(collection);
                break;
            case SEQUENTIAL:
                dataContainer = new DataContainerSequential(collection);
                break;
            case UNIQUE_SEQUENTIAL:
                dataContainer = new DataContainerUnique(type, collection);
                break;
            case UNIQUE_RANDOM:
                dataContainer = new DataContainerUnique(type, collection);
                break;
            default:
                throw new RuntimeException("Can't create DataContainer", new IllegalArgumentException("Wrong RequestType format!"));

        }
        return dataContainer;
    }
}
