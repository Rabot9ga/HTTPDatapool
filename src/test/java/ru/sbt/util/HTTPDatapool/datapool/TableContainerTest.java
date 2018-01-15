package ru.sbt.util.HTTPDatapool.datapool;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.sbt.util.HTTPDatapool.httpapi.ParametersTable;
import ru.sbt.util.HTTPDatapool.paramsContainer.DataContainerFactory;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.*;

public class TableContainerTest {


    TableContainer tableContainer;
    private Set<String> columns;


    @BeforeClass
    public void setUp() throws Exception {
        columns = new HashSet<>();
        columns.add("123");
        columns.add("31");
        columns.add("231");

        tableContainer = TableContainer.builder()
                .container(DataContainerFactory.create(RequestType.RANDOM, new ArrayList<>()))
                .tableName("Script1")
                .columnsName(columns)
                .scriptName("Script1")
                .build();

    }

    @Test
    public void testIsFit() throws Exception {
        Set<String> columns = new HashSet<>();
        columns.add("123");
        columns.add("31");
        columns.add("231");

        ParametersTable table = ParametersTable.builder()
                .columnsName(columns)
                .tableName("Script1")
                .scriptName("Script1")
                .type(RequestType.RANDOM).build();

        Assert.assertTrue(tableContainer.isFit(table));
    }
}