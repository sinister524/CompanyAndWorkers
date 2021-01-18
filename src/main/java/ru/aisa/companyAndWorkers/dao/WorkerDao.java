package ru.aisa.companyAndWorkers.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import ru.aisa.companyAndWorkers.entity.Company;
import ru.aisa.companyAndWorkers.entity.Worker;
import ru.aisa.companyAndWorkers.exception.ValueNotUniqueException;
import ru.aisa.companyAndWorkers.repository.CompanyRepository;
import ru.aisa.companyAndWorkers.repository.WorkerRepository;
import ru.aisa.companyAndWorkers.row_mapper.WorkerRowMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public class WorkerDao implements WorkerRepository {

    private final WorkerRowMapper workerRowMapper;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final CompanyRepository companyRepository;

    public WorkerDao(WorkerRowMapper workerRowMapper, JdbcTemplate jdbcTemplate, CompanyRepository companyRepository) {
        this.workerRowMapper = workerRowMapper;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.companyRepository = companyRepository;
    }

    @Override
    public List<Worker> findAll() {
        List<Worker> workers = namedParameterJdbcTemplate.query("SELECT * FROM public.worker", workerRowMapper);
        return workers.stream()
                .peek(worker -> worker.setCompany(companyRepository.findById(worker.getCompany().getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Worker> findAllCompanyWorkers(Company company) {
        List<Worker> workers = namedParameterJdbcTemplate.query(
                "SELECT * FROM public.worker WHERE company_id = " + company.getId(),
                workerRowMapper);
        return workers.stream()
                .peek(worker -> worker.setCompany(companyRepository.findById(worker.getCompany().getId())))
                .collect(Collectors.toList());
    }

    @Override
    public Worker findById(Long id) {
        SqlParameterSource requiredId = new MapSqlParameterSource().addValue("id", id);
        Optional<Worker> optionalWorker = Optional.ofNullable(
                namedParameterJdbcTemplate.queryForObject("SELECT * FROM public.worker WHERE id = :id",
                requiredId, workerRowMapper));
        Worker worker = optionalWorker.orElseThrow(NoSuchElementException::new);
        worker.setCompany(companyRepository.findById(worker.getCompany().getId()));
        return worker;
    }

    @Override
    public Worker findByEmail(String email) {
        SqlParameterSource requiredEmail = new MapSqlParameterSource().addValue("email", email);
        Long workerId = namedParameterJdbcTemplate.queryForObject("SELECT id FROM public.worker WHERE email = :email",
                requiredEmail, Long.class);
        return findById(workerId);
    }

    @Override
    public void delete(Long id) {
        SqlParameterSource requiredIdParameter = new MapSqlParameterSource().addValue("id", id);
        int deleteStatus = namedParameterJdbcTemplate.update("DELETE FROM public.worker WHERE id = :id",
                requiredIdParameter);
        if (deleteStatus == 0) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void clear() {
        SqlParameterSource emptyParameter = new MapSqlParameterSource();
        namedParameterJdbcTemplate.update("DELETE FROM public.worker", emptyParameter);
    }

    @Override
    public void delete(String email) {
        SqlParameterSource requiredEmailParameter = new MapSqlParameterSource().addValue("email", email);
        int deleteStatus = namedParameterJdbcTemplate.update("DELETE FROM public.worker WHERE email = :email",
                requiredEmailParameter);
        if (deleteStatus == 0) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public Worker save(Worker worker) {
        SqlParameterSource parametersForInsert = new MapSqlParameterSource()
                .addValue("name", worker.getName())
                .addValue("birthday", worker.getBirthday())
                .addValue("email", worker.getEmail())
                .addValue("company_id", worker.getCompany().getId());
        int saveStatus = namedParameterJdbcTemplate.update("INSERT INTO public.worker (name, birthday, email, company_id) " +
                "VALUES (:name, :birthday, :email, :company_id)", parametersForInsert);
        if (saveStatus == 0) {
            throw new ValueNotUniqueException("Работник с таким Email уже существует");
        }
        return findByEmail(worker.getEmail());
    }

    @Override
    public Worker update(Worker worker) {
        SqlParameterSource parametersForUpdate = new MapSqlParameterSource()
                .addValue("id", worker.getId())
                .addValue("name", worker.getName())
                .addValue("birthday", worker.getBirthday())
                .addValue("email", worker.getEmail())
                .addValue("company_id", worker.getCompany().getId());
        int updateStatus = namedParameterJdbcTemplate.update("UPDATE public.worker " +
                "SET name = :name, birthday = :birthday, email =:email, company_id = :company_id " +
                "WHERE id = :id", parametersForUpdate);
        if (updateStatus == 0) {
            throw new ValueNotUniqueException("Работник с таким Email уже существует");
        }
        return findById(worker.getId());
    }
}
