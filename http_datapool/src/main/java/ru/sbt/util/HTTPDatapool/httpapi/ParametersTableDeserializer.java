package ru.sbt.util.HTTPDatapool.httpapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ParametersTableDeserializer extends KeyDeserializer {
    private final Pattern pattern = Pattern.compile("ParametersTable\\(type=(.*?), tableName=(.*?), columnsName=(.*?), scriptName=(.*?)\\)");

    @Override
    public ParametersTable deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Matcher matcher = pattern.matcher(key);
        if (matcher.matches()) {

            String columnsName = matcher.group(3);

            String[] columns = columnsName.substring(1, columnsName.length() - 1).split(",");
            Set<String> columnsSet = Arrays.stream(columns)
                    .map(String::trim)
                    .collect(Collectors.toSet());

            return ParametersTable.builder()
                    .type(RequestType.valueOf(matcher.group(1)))
                    .tableName(matcher.group(2))
                    .scriptName(matcher.group(4))
                    .columnsName(columnsSet)
                    .build();

        }
        throw new RuntimeException("Can't deserialize key: " + key);
    }
}
