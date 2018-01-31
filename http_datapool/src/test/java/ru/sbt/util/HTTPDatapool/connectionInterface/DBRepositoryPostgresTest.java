package ru.sbt.util.HTTPDatapool.connectionInterface;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;


@Slf4j
@SpringBootTest(properties = "logging.level.ru.sbt.util.HTTPDatapool=debug")
public class DBRepositoryPostgresTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    DBRepository dbRepository;

    @Test
    public void testGetFromTableBetween() throws Exception {
        List<Map<String, Object>> test_table = dbRepository.getFromTableBetween("TEST_TABLE", 100, 199);
        log.debug("test_table: {}", test_table);
        log.debug("test_table.size() = {}", test_table.size());
        Assert.assertTrue(test_table.size() == 100);
    }

    @Test
    public void testGetTableSize() throws Exception {
        int test_table = dbRepository.getTableSize("TEST_TABLE");
        log.debug("test_table.size = {}", test_table);
        Assert.assertEquals(test_table, 11291692);
    }

    @Test
    public void testGetAllTableNamesInDB() throws Exception {
        List<String> namesInDB = dbRepository.getAllTableNamesInDB();
        namesInDB.forEach(s -> log.debug("name = {}", s));
        Assert.assertTrue(namesInDB.size() > 0);
    }
}