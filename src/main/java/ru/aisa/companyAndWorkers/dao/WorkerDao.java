package ru.aisa.companyAndWorkers.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.aisa.companyAndWorkers.entity.Worker;
import ru.aisa.companyAndWorkers.repository.WorkerRepository;
import ru.aisa.companyAndWorkers.row_mapper.CompanyRowMapper;
import ru.aisa.companyAndWorkers.row_mapper.WorkerRowMapper;

import java.util.List;

@Repository
public class WorkerDao implements WorkerRepository {

    private final CompanyRowMapper companyRowMapper;
    private final WorkerRowMapper workerRowMapper;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final CompanyDao companyDao;

    public WorkerDao(CompanyRowMapper companyRowMapper, WorkerRowMapper workerRowMapper, JdbcTemplate jdbcTemplate, CompanyDao companyDao) {
        this.companyRowMapper = companyRowMapper;
        this.workerRowMapper = workerRowMapper;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.companyDao = companyDao;
    }

    @Override
    public List<Worker> findAll() {
        return null;
    }

    @Override
    public Worker findById(Long id) {
        return null;
    }

    @Override
    public Worker findByInn(String inn) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void delete(String inn) {

    }

    @Override
    public Worker save(Worker worker) {
        return null;
    }

    @Override
    public Worker update(Worker worker) {
        return null;
    }
}
