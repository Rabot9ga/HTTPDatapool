package ru.sbt.util.HTTPDatapool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.sbt.util.HTTPDatapool.repository.DataRepository;


@SpringBootApplication
public class HttpDatapoolApplication {

    @Autowired

    DataRepository dataRepository;

    public static void main(String[] args) {
        SpringApplication.run(HttpDatapoolApplication.class, args);
    }

}
