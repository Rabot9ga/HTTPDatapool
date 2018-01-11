package ru.sbt.util.HTTPDatapool.paramsContainer;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.Generator;

@Slf4j
public class ContainerAddingTest extends Assert {
    DataContainerAPI paramsContainerRandom;
    DataContainerAPI paramsContainerSequential;

    private int paramsContainerRandomSize = 3;

    @Before
    public void init() {
        paramsContainerRandom = Generator.fillDataContainerRandom(paramsContainerRandomSize);
        paramsContainerSequential = Generator.fillDataContainerSequential(paramsContainerRandomSize);
    }


    @Test
    public void gettingRandomTest() {
        gettingTest(RequestType.RANDOM);
    }

    @Test
    public void gettingSequentialTest() {
        gettingTest(RequestType.SEQUENTIAL);
    }

    void gettingTest(RequestType type) {
        assertTrue("paramsContainer size is not " + paramsContainerRandomSize + "!", paramsContainerRandom.getSize() == paramsContainerRandomSize);

        String prevValue = paramsContainerRandom.getRow().get("id");

        // number of occurrences when data is returning sequentially
        int numOfSequentials = 0;

        // number of repeats during two requests
        int numOfRepeats = 0;

        int numOfGets = paramsContainerRandom.getSize() * 4;


        for (int i = 0; i < numOfGets; i++) {

            if (paramsContainerRandom.getRow().get("id").equals(prevValue)) numOfRepeats++;
            if (Integer.parseInt(paramsContainerRandom.getRow().get("id")) + 1 == Integer.parseInt(prevValue))
                numOfSequentials++;

            prevValue = paramsContainerRandom.getRow().get("id");
        }

        log.debug("\n == {} == \n NumOfGets: {} \n Number of how many times data returned sequentially = {} " +
                "\n numOfRepeats : {}", type, numOfGets, numOfSequentials, numOfRepeats);

        switch (type) {
            case RANDOM: {
                assertTrue("data returning Sequentially!", numOfSequentials < numOfGets);
                assertTrue("Returning is the same all the time!", numOfRepeats < numOfGets);
                break;
            }
            case SEQUENTIAL: {
                assertTrue("data doesn't return Sequentially!", numOfSequentials == numOfGets);
                assertTrue("Returning is the same all the time!", numOfRepeats < numOfGets);
                break;
            }
        }
    }

}
