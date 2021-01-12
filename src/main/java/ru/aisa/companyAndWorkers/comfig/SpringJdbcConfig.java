package ru.aisa.companyAndWorkers.comfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("ru.aisa.companyAndWorkers")
public class SpringJdbcConfig {

    @Bean
    public DataSource postgreDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost/company_and_workers");
        dataSource.setUsername("postgres");
        dataSource.setPassword("05241369");

        return dataSource;
    }
}
