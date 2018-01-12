package ru.sbt.util.HTTPDatapool.paramsContainer.utils;

import lombok.extern.slf4j.Slf4j;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.TestResult;

import java.util.Map;

@Slf4j
public class TestUtils {

    public static TestResult gettingTest(DataContainerAPI dataContainer, int size) {

        // number of occurrences when data is returning sequentially
        int numOfSequentials = 0;

        // number of repeats during two requests
        int numOfRepeats = 0;

        TestResult testResult = new TestResult();

        if (dataContainer.getSize() != size) {
            throw new AssertionError("paramsContainer size is not " + size + "!");
        }

        Map<String, String> row0 = dataContainer.getRow();
        String prevValue = row0.get("id");
        log.trace("In test of {}. Returned id: {}", dataContainer.getRequestType(), row0.get("id"));

        int numOfGets = dataContainer.getSize() * 4;

        for (int i = 0; i < numOfGets; i++) {

            Map<String, String> row = dataContainer.getRow();

            log.trace("In test of {}. Returned id: {}", dataContainer.getRequestType(), row.get("id"));

            if (row.get("id").equals(prevValue)) numOfRepeats++;
            log.trace("currRow id: {} / prevValue id: {}", row.get("id"), prevValue);
            if (Integer.parseInt(row.get("id")) == ((Integer.parseInt(prevValue) + 1) % size))
                numOfSequentials++;

            prevValue = row.get("id");
        }

        log.trace("\n == {} == \n NumOfGets: {} \n Number of how many times data returned sequentially = {} " +
                "\n numOfRepeats : {}", dataContainer.getRequestType(), numOfGets, numOfSequentials, numOfRepeats);

        testResult.withNumOfGetts(numOfGets)
                .withNumOfRepeats(numOfRepeats)
                .withNumOfSequentials(numOfSequentials);

        return testResult;
    }


}
