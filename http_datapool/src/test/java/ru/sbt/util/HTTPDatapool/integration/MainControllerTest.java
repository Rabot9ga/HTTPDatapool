package ru.sbt.util.HTTPDatapool.integration;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.sbt.util.HTTPDatapool.httpdto.RequestType;
import ru.sbt.util.HTTPDatapool.httpdto.Status;
import ru.sbt.util.HTTPDatapool.httpparameter.HttpParameter;
import ru.sbt.util.HTTPDatapool.httpparameter.ParameterList;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
public class MainControllerTest extends AbstractTransactionalTestNGSpringContextTests {

    @LocalServerPort
    int port;


    @BeforeClass
    public void setUp() throws Exception {
    }

    @Test
    public void getParameter() throws IOException, InterruptedException {

        Status status;
        ParameterList parameters;

        while (true) {
            parameters = HttpParameter.getInstance("http://localhost:" + port)
                    .addRequest("FORTEST", RequestType.RANDOM, "testScript", "COLUMN1", "COLUMN2", "COLUMN3", "COLUMN4")
//                .addRequest("TABLE1", RequestType.RANDOM, "testScript", "COLUMN1", "COLUMN2", "COLUMN3", "COLUMN4")
//                .addRequest("TABLE2", RequestType.RANDOM, "testScript", "COLUMN1", "COLUMN2", "COLUMN3", "COLUMN4")
//                .addRequest("TABLE3", RequestType.RANDOM, "testScript", "COLUMN1", "COLUMN2", "COLUMN3", "COLUMN4")
                    .getParameters();

            log.debug("parameters: {}", parameters);

            status = parameters.getStatus();

            Assert.assertNotEquals(status, Status.ERROR, "status is error!");

            if (!status.equals(Status.BUSY)) {
                break;
            }
            TimeUnit.SECONDS.sleep(1);
        }


        Assert.assertEquals(status, Status.SUCCESS);
        parameters.getParameters().forEach(map -> Assert.assertNotEquals(map, null));


    }
}