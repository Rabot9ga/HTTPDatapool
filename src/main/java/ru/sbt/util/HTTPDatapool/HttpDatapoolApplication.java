package ru.sbt.util.HTTPDatapool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;

import java.util.List;
import java.util.Map;

import static java.lang.System.exit;

@SpringBootApplication
public class HttpDatapoolApplication implements CommandLineRunner {

//	@Autowired
//	DataSource dataSource;

	@Autowired
	DataRepository dataRepository;

	public static void main(String[] args) {
		SpringApplication.run(HttpDatapoolApplication.class, args);


	}

	@Override
	public void run(String... args) throws Exception {



		System.out.println("Display all customers...");
		List<Map<String, Object>> result = dataRepository.getTable("ID_FIO_DOC_INDIVIDUAL");


		System.out.println("Done!");

		exit(0);
	}
}
