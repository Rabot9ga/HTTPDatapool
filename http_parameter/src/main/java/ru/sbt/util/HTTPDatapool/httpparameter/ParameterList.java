package ru.sbt.util.HTTPDatapool.httpparameter;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ParameterList {

    private List<Map<String,String>> parameters;

    public ParameterList(List<Map<String, String>> parameters) {
        this.parameters = parameters;
    }

    public Map<String, String> getRequestValue(int i) {
        if (i > parameters.size() - 1) throw new RuntimeException("i must be < parameters.size() - 1");

        if (i < 0) throw new RuntimeException("i must be > 0");


        // TODO: 22.01.2018 сделать проверку на то что данные пришли
        return parameters.get(i);
    }
}
