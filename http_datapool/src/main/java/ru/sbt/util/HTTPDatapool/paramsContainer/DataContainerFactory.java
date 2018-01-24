package ru.sbt.util.HTTPDatapool.paramsContainer;

import ru.sbt.util.HTTPDatapool.httpapi.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers.DataContainerRandom;
import ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers.DataContainerSequential;
import ru.sbt.util.HTTPDatapool.paramsContainer.dataContainers.DataContainerUnique;

import java.util.List;

/**
 * Class for providing composite object with ready for consuming structured parametrization
 *
 * @author SBT-Muravev-AA
 */
public class DataContainerFactory {

    public static <T> DataContainerAPI<T> create(RequestType type) {
        return createContainer(type, null);
    }

    public static <T> DataContainerAPI<T> create(RequestType type, List<T> collection) {
        return createContainer(type, collection);
    }

    private static <T> DataContainerAPI<T> createContainer(RequestType type, List<T> collection){
        DataContainerAPI<T> dataContainer;

        switch (type) {
            case RANDOM:
                dataContainer = new DataContainerRandom<T>(collection);
                break;
            case SEQUENTIAL:
                dataContainer = new DataContainerSequential<T>(collection);
                break;
            case UNIQUE_SEQUENTIAL:
                dataContainer = new DataContainerUnique<T>(type, collection);
                break;
            case UNIQUE_RANDOM:
                dataContainer = new DataContainerUnique<T>(type, collection);
                break;
            default:
                throw new RuntimeException("Can't create DataContainer", new IllegalArgumentException("Wrong RequestType format!"));

        }
        return dataContainer;
    }
}
