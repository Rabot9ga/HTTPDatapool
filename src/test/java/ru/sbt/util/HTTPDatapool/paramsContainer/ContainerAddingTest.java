package ru.sbt.util.HTTPDatapool.paramsContainer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;
import ru.sbt.util.HTTPDatapool.paramsContainer.utils.Generator;

public class ContainerAddingTest extends Assert {
    ParamsContainer paramsContainerRandom;
    ParamsContainer paramsContainerSequential;

    private int paramsContainerRandomSize = 3;

    @Before
    public void init() {
        paramsContainerRandom = Generator.fillParamsContainerRandom(paramsContainerRandomSize);
        paramsContainerSequential = Generator.fillParamsContainerSequential(paramsContainerRandomSize);
    }


    @Test
    public void gettingRandomTest() {
        gettingTest(RequestType.RANDOM);
    }

    @Test
    public void gettingSequentialTest() {
        gettingTest(RequestType.SEQUENTIAL);
    }

    void gettingTest(RequestType type){
        assertTrue("paramsContainer size is not " + paramsContainerRandomSize + "!", paramsContainerRandom.getSize() == paramsContainerRandomSize);

        String prevValue = paramsContainerRandom.getRow().get("id");

        // number of occurrences when data is returning sequentially
        int numOfSequentials = 0;

        // number of repeats during two requests
        int numOfRepeats = 0;

        int numOfGetts = paramsContainerRandom.getSize() * 4;


        for (int i = 0; i < numOfGetts; i++) {

            if (paramsContainerRandom.getRow().get("id").equals(prevValue)) numOfRepeats++;
            if (Integer.parseInt(paramsContainerRandom.getRow().get("id")) + 1 == Integer.parseInt(prevValue))
                numOfSequentials++;

            prevValue = paramsContainerRandom.getRow().get("id");
        }

        switch (type){
            case RANDOM:{
                assertTrue("data returning Sequentially!", numOfSequentials < numOfGetts);
                assertTrue("Returning is the same all the time!", numOfRepeats < numOfGetts);
                break;
            }
            case SEQUENTIAL:{
                assertTrue("data doesn't return Sequentially!", numOfSequentials == numOfGetts);
                assertTrue("Returning is the same all the time!", numOfRepeats < numOfGetts);
                break;
            }
        }

    }

}
