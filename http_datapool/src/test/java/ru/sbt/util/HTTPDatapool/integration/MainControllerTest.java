package ru.sbt.util.HTTPDatapool.integration;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.sbt.util.HTTPDatapool.httpapi.RequestType;
import ru.sbt.util.HTTPDatapool.httpparameter.HttpParameter;
import ru.sbt.util.HTTPDatapool.httpparameter.ParameterList;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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


//        for (int i = 0; i < 100; i++) {
//            ParameterList parameters = HttpParameter.getInstance("http://localhost:" + port)
//                    .addRequest("FORTEST", RequestType.RANDOM, "testScript", "ID", "NAME", "AGE", "PHONE")
//                    .getParameters();
//            System.out.println("parameters = " + parameters);
//            Thread.sleep(1000);
//
//        }

        ExecutorService service = Executors.newFixedThreadPool(1);



        Callable<ParameterList> callable = () -> HttpParameter.getInstance("http://localhost:" + port)
//                .addRequest("TABLE1", RequestType.RANDOM, "testScript", "COLUMN1", "COLUMN2", "COLUMN3", "COLUMN4")
                .addRequest("TABLE2", RequestType.RANDOM, "testScript", "COLUMN1", "COLUMN2", "COLUMN3", "COLUMN4")
//                .addRequest("TABLE3", RequestType.RANDOM, "testScript", "COLUMN1", "COLUMN2", "COLUMN3", "COLUMN4")
                .getParameters();


        List<Future<ParameterList>> collect = IntStream.range(0, 100000000).mapToObj(value -> service.submit(callable)).collect(Collectors.toList());

        for (Future<ParameterList> parameterListFuture : collect) {
            ParameterList parameterList = null;
            try {
                parameterList = parameterListFuture.get();
            } catch (InterruptedException | ExecutionException e) {

            }
            System.out.println("parameterList = " + parameterList);

        }





    }
}