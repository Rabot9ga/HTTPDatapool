//package ru.sbt.util.HTTPDatapool.paramsContainer;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import ru.sbt.util.HTTPDatapool.paramsContainer.api.DataContainerAPI;
//import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;
//import ru.sbt.util.HTTPDatapool.paramsContainer.utils.Generator;
//
//import java.util.Map;
//
//@Slf4j
//public class ContainerAddingTest extends Assert {
//    DataContainerAPI paramsContainerRandom;
//    DataContainerAPI paramsContainerSequential;
//
////    private int paramsContainerRandomSize = 3;
//
////    @Before
////    public void init() {
////        paramsContainerRandom = Generator.fillDataContainerRandom(paramsContainerRandomSize);
////        paramsContainerSequential = Generator.fillDataContainerSequential(paramsContainerRandomSize);
////    }
//
//
////    @Test
////    public void gettingRandomTest() {
////        gettingTest(RequestType.RANDOM);
////    }
////
////    @Test
////    public void gettingSequentialTest() {
////        gettingTest(RequestType.SEQUENTIAL);
////    }
//
////    public void gettingTest(RequestType type) {
//    public void gettingTest(DataContainerAPI dataContainer, int size) {
//        switch (dataContainer.getRequestType()) {
//            case RANDOM:
//                dataContainer = paramsContainerRandom;
//                break;
//            case SEQUENTIAL:
//                dataContainer = paramsContainerSequential;
//                break;
//            default:
//                throw new RuntimeException("You have chosen unavailable type");
//        }
//
//
//        assertTrue("paramsContainer size is not " + size + "!", dataContainer.getSize() == size);
//
//        Map<String, String> row0 = dataContainer.getRow();
//        String prevValue = row0.get("id");
//        log.debug("In test of {}. Returned id: {}", dataContainer.getRequestType(), row0.get("id"));
//
//        // number of occurrences when data is returning sequentially
//        int numOfSequentials = 0;
//
//        // number of repeats during two requests
//        int numOfRepeats = 0;
//
//        int numOfGets = dataContainer.getSize() * 4;
//
//
//        for (int i = 0; i < numOfGets; i++) {
//
//            Map<String, String> row = dataContainer.getRow();
//
//            log.debug("In test of {}. Returned id: {}", dataContainer.getRequestType(), row.get("id"));
//
//            if (row.get("id").equals(prevValue)) numOfRepeats++;
//            log.debug("currRow id: {} / prevValue id: {}", row.get("id"), prevValue);
//            if (Integer.parseInt(row.get("id")) == ((Integer.parseInt(prevValue) + 1) % size))
//                numOfSequentials++;
//
//            prevValue = row.get("id");
//        }
//
//        log.debug("\n == {} == \n NumOfGets: {} \n Number of how many times data returned sequentially = {} " +
//                "\n numOfRepeats : {}", dataContainer.getRequestType(), numOfGets, numOfSequentials, numOfRepeats);
//
//        switch (dataContainer.getRequestType()) {
//            case RANDOM: {
//                assertTrue("data returning Sequentially!", numOfSequentials < numOfGets);
//                assertTrue("Returning is the same all the time!", numOfRepeats < numOfGets);
//                break;
//            }
//            case SEQUENTIAL: {
//                assertTrue("data doesn't return Sequentially!", numOfSequentials == numOfGets);
//                if (size > 1)
//                    assertTrue("Returning is the same all the time!", numOfRepeats == 0);
//                break;
//            }
//        }
//    }
//
//}
