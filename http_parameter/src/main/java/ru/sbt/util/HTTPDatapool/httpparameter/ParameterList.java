package ru.sbt.util.HTTPDatapool.httpparameter;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ParameterList {

    private List<Map<String, Object>> parameters;

    public ParameterList(List<Map<String, Object>> parameters) {
        this.parameters = parameters;
    }

    public Map<String, Object> getRequestValue(int i) {
        if (i > parameters.size() - 1) throw new RuntimeException("i must be < parameters.size() - 1");

        if (i < 0) throw new RuntimeException("i must be > 0");

        return parameters.get(i);
    }

    public Map<String, String> getStringRequestValue(int i){
        HashMap<String, String> map = new HashMap<>();
        getRequestValue(i).forEach((s, o) -> map.put(s, o.toString()));
        return map;
    }
}
