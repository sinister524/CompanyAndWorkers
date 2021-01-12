package ru.aisa.companyAndWorkers.row_mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.aisa.companyAndWorkers.entity.Worker;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class WorkerRowMapper implements RowMapper<Worker> {

    @Override
    public Worker mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Worker(resultSet.getLong("id"), resultSet.getString("name"), resultSet.getString("birthday"),
                resultSet.getString("email"), new File(resultSet.getString("photo")));
    }
}
