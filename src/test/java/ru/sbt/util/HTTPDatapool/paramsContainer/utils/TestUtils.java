package ru.sbt.util.HTTPDatapool.paramsContainer.utils;

import lombok.extern.slf4j.Slf4j;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.TestResult;

import java.util.Map;

@Slf4j
public class TestUtils {

    public static TestResult gettingTest(DataContainerAPI dataContainer, int size) {
        int checkFactor;

        if (size < 20) checkFactor = 4;
        else checkFactor = 1;

        // number of gets from dataContainer
        int numOfGets = dataContainer.getSize() * checkFactor;

        // number of occurrences when data is returning sequentially
        int numOfSequentials = 0;

        // number of repeats during two requests
        int numOfRepeats = 0;

        if (dataContainer.getSize() != size) {
            throw new AssertionError("paramsContainer size is not " + size + "!");
        }

        Map<String, String> row0 = dataContainer.getRow();
        String prevValue = row0.get("id");
        log.trace("In test of {}. Returned id: {}", dataContainer.getRequestType(), row0.get("id"));


        Map<String, String> row;
        for (int i = 0; i < numOfGets; i++) {

            row = dataContainer.getRow();

            log.trace("In test of {}. Returned id: {}", dataContainer.getRequestType(), row.get("id"));

            if (row.get("id").equals(prevValue)) numOfRepeats++;

            log.trace("currRow id: {} / prevValue id: {}", row.get("id"), prevValue);

            if (Integer.parseInt(row.get("id")) == ((Integer.parseInt(prevValue) + 1) % size)) {
                numOfSequentials++;
            }

            prevValue = row.get("id");
        }

        log.trace("\n == {} == \n NumOfGets: {} \n Number of how many times data returned sequentially = {} " +
                "\n numOfRepeats : {}", dataContainer.getRequestType(), numOfGets, numOfSequentials, numOfRepeats);

        TestResult testResult = TestResult.builder()
                .numOfGets(numOfGets)
                .numOfRepeats(numOfRepeats)
                .numOfSequentials(numOfSequentials)
                .build();

        return testResult;
    }


}
