package ru.sbt.util.HTTPDatapool;


import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sbt.util.HTTPDatapool.repository.DataRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


@SpringBootTest
public class DataRepositoryTest extends AbstractTransactionalTestNGSpringContextTests{



    @Autowired
    DataRepository dataRepository;

    @Test
    public void getSomeColumnFromTableBetween() {

        HashSet<String> columnNames =  new HashSet<> (Arrays.asList("ID","NAME"));
        List<Map<String, String>> result = dataRepository.getDataFromCacheBetween("FORTEST",columnNames,2,3);
        assertTrue( (result.size()==2)&& (result.get(0).get("ID").equals("124"))&&(result.get(1).get("NAME").equals("NDDF")));

    }

    @Test
    public void getSomeColumnFromTable() {


        HashSet<String> columnNames =  new HashSet<> (Arrays.asList("PARTY_ID","NAME"));
        List<Map<String, String>> result = dataRepository.getDataFromCache("ID_FIO_DOC_INDIVIDUAL",columnNames);
        assertTrue(result.size()>0);

    }

}