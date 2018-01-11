package ru.sbt.util.HTTPDatapool.paramsContainer;

import ru.sbt.util.HTTPDatapool.paramsContainer.api.ParamsContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.util.Map;

/**
 * Class for providing composite object with ready for consuming structured parametrization
 *
 * @author SBT-Muravev-AA
 *
 */
public class ParamsContainer implements ParamsContainerAPI {
    private RequestType requestType;

    private TypesContainer typesContainer = new TypesContainer();

    public ParamsContainer(RequestType requestType) {
        this.requestType = requestType;
    }

    @Override
    public RequestType getRequestType() {
        return requestType;
    }

    @Override
    public void setRequestType(RequestType type) {
        this.requestType = type;
    }

    @Override
    public <T> T getRow() {
        return null;
    }

    @Override
    public <T extends Map<String, String>> void addRow(T t) {

    }
}
