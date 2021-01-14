package ru.aisa.companyAndWorkers.row_mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.aisa.companyAndWorkers.entity.Company;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyRowMapper implements RowMapper<Company> {

    @Override
    public Company mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Company(resultSet.getLong("id"), resultSet.getString("name"), resultSet.getString("inn"),
                resultSet.getString("phone_number"), resultSet.getString("address"));
    }
}