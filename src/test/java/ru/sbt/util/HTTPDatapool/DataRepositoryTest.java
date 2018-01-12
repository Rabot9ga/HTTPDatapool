package ru.sbt.util.HTTPDatapool;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.sbt.util.HTTPDatapool.repository.DataRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DataRepositoryTest {


    @Autowired
    DataRepository dataRepository;

    @Test
    public void getTable() {

//        List<Map<String, Object>> result = dataRepository.getTable("ID_FIO_DOC_INDIVIDUAL");
//        assertTrue(result.size()>0);

    }

    @Test
    public void getSomeColumnFromTable() {


//        HashSet<String> columnNames =  new HashSet<> (Arrays.asList("PARTY_ID","NAME"));
//        List<Map<String, Object>> result = dataRepository.getSomeColumnFromTable("ID_FIO_DOC_INDIVIDUAL",columnNames);
//        assertTrue(result.size()>0);

    }

}