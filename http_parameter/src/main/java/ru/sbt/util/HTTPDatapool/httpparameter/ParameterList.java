package ru.sbt.util.HTTPDatapool.httpparameter;

import lombok.Getter;
import lombok.ToString;
import ru.sbt.util.HTTPDatapool.httpdto.Status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString
public class ParameterList {

    @Getter
    private Status status;

    @Getter
    private List<Map<String, Object>> parameters;

    public ParameterList(List<Map<String, Object>> parameters, Status status) {
        this.parameters = parameters;
        this.status = status;
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
