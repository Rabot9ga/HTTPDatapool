package ru.sbt.util.HTTPDatapool.httpparameter;

import retrofit2.Response;
import ru.sbt.util.HTTPDatapool.httpdto.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RequestAdder {

    private final MainController controller;
    private LinkedHashSet<ParametersTable> requestSet = new LinkedHashSet<>();

    public RequestAdder(MainController controller) {
        this.controller = controller;
    }

    public RequestAdder addRequest(String tableName, RequestType requestType, String scriptName, String... columnNames) {

        Set<String> columnsName = Arrays.stream(columnNames).collect(Collectors.toSet());
        ParametersTable parametersTable = ParametersTable.builder()
                .columnsName(columnsName)
                .scriptName(scriptName)
                .tableName(tableName)
                .type(requestType)
                .build();

        if (requestSet.contains(parametersTable)) {
            throw new RuntimeException("Duplicate parameters request");
        }

        requestSet.add(parametersTable);
        return this;
    }

    public ParameterList getParameters() {
        DatapoolRequest datapoolRequest = DatapoolRequest.builder()
                .parametersTables(requestSet)
                .build();

        Response<DatapoolResponse> response;
        try {
            response = controller.getParameter(datapoolRequest).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return new ParameterList(new ArrayList<>(), Status.ERROR);
        }


        Status status;
        if (!response.isSuccessful()) {
            return new ParameterList(new ArrayList<>(), Status.ERROR);
        }


        DatapoolResponse body = response.body();
        Map<ParametersTable, ResponseTables> responseTables = body.getResponseTables();

        if (responseTables.entrySet().stream().anyMatch(entry -> entry.getValue().getStatus().equals(Status.BUSY))) {
            status = Status.BUSY;
        } else {
            status = Status.SUCCESS;
        }

        List<Map<String, Object>> list = requestSet.stream()
                .map(responseTables::get)
                .map(ResponseTables::getMapParameters)
                .collect(Collectors.toList());

        return new ParameterList(list, status);
    }
}
