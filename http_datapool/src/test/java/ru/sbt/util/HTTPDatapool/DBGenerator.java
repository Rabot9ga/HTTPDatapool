package ru.sbt.util.HTTPDatapool;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
@Slf4j
public class DBGenerator extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    JdbcTemplate jdbcTemplate;


    @Test(enabled = false)
    @Rollback(false)
    public void generateData() throws Exception {

        String QUERY = "INSERT INTO public.\"TABLE3\" (\"COLUMN1\", \"COLUMN2\", \"COLUMN3\", \"COLUMN4\") " +
                "VALUES (?, ?, ?, ?);";

        ExecutorService service = Executors.newFixedThreadPool(10);

        Runnable runnable = () -> jdbcTemplate.batchUpdate(QUERY, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, getUUID());
                ps.setString(2, getUUID());
                ps.setString(3, getUUID());
                ps.setInt(4, ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
            }

            @Override
            public int getBatchSize() {
                return 10000;
            }
        });


        List<? extends Future<?>> collect = IntStream.range(0, 1000).mapToObj(value -> service.submit(runnable)).collect(Collectors.toList());
//
        collect.forEach(o -> {
            try {
                o.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });


    }

    private String getSql() {
        return "INSERT INTO public.\"TEST_TABLE\" (\"COLUMN1\", \"COLUMN2\", \"COLUMN3\", \"COLUMN4\") " +
                "VALUES (" + getUUID() + "," + getUUID() + "," + getUUID() + "," + ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE) + ");";
    }

    private String getUUID() {
        return "\'" + UUID.randomUUID().toString() + "\'";
    }


}
