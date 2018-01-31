package ru.sbt.util.HTTPDatapool.connectionInterface;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ConfigDBRepo {

    // TODO: 31.01.2018 Дописать конфигурацию для оракла
    @Bean
    @Autowired
    public DBRepository dbRepository(JdbcTemplate jdbcTemplate) {
        return new DBRepositoryPostgres(jdbcTemplate);
    }
}
