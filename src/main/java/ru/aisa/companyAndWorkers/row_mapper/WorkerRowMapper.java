package ru.aisa.companyAndWorkers.row_mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.aisa.companyAndWorkers.entity.Company;
import ru.aisa.companyAndWorkers.entity.Worker;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkerRowMapper implements RowMapper<Worker> {

    @Override
    public Worker mapRow(ResultSet resultSet, int i) throws SQLException {
        Company company = new Company();
        company.setId(resultSet.getLong("company_id"));
        return new Worker(resultSet.getLong("id"), resultSet.getString("name"), resultSet.getDate("birthday"),
                resultSet.getString("email"), new File(resultSet.getString("photo")), company);
    }
}
