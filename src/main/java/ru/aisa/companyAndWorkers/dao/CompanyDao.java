package ru.aisa.companyAndWorkers.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.aisa.companyAndWorkers.entity.Company;
import ru.aisa.companyAndWorkers.repository.CompanyRepository;
import ru.aisa.companyAndWorkers.row_mapper.CompanyRowMapper;
import ru.aisa.companyAndWorkers.row_mapper.WorkerRowMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CompanyDao implements CompanyRepository {

    private final CompanyRowMapper companyRowMapper;
    private final WorkerRowMapper workerRowMapper;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CompanyDao(CompanyRowMapper companyRowMapper, WorkerRowMapper workerRowMapper, JdbcTemplate jdbcTemplate) {
        this.companyRowMapper = companyRowMapper;
        this.workerRowMapper = workerRowMapper;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public List<Company> findAll() {
        List<Company> companies = namedParameterJdbcTemplate.query("SELECT * FROM public.company", companyRowMapper);
        return companies.stream()
                .peek(company -> {
                    SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("company_id", company.getId());
                    company.setWorkers(namedParameterJdbcTemplate.query("SELECT * FROM public.worker " +
                            "WHERE company_id = :account_id", namedParameters, workerRowMapper));
                })
                .collect(Collectors.toList());
    }

    @Override
    public Company findById(Long id) {
        SqlParameterSource searchIdParameters = new MapSqlParameterSource().addValue("id", id);
        Optional<Company> optionalCompany = Optional.ofNullable(
                namedParameterJdbcTemplate.queryForObject("SELECT * FROM public.company WHERE id = :id",
                        searchIdParameters, Company.class));
        Company company = optionalCompany.orElseThrow(NoSuchElementException::new);
        SqlParameterSource companyIdParameter = new MapSqlParameterSource().addValue("company_id", company.getId());
        company.setWorkers(namedParameterJdbcTemplate.query("SELECT * FROM public.worker " +
                "WHERE company_id = :account_id", companyIdParameter, workerRowMapper));
        return company;
    }

    @Override
    public Company findByInn(String inn) {
        SqlParameterSource companyInnParameter = new MapSqlParameterSource().addValue("inn", inn);
        Long companyId = namedParameterJdbcTemplate.queryForObject("SELECT id FROM public.company WHERE inn = :inn",
                companyInnParameter, Long.class);
        return findById(companyId);
    }

    @Override
    public void delete(Long id) {
        SqlParameterSource companyIdParameter = new MapSqlParameterSource().addValue("id", id);
        int deleteStatus = namedParameterJdbcTemplate.update("DELETE FROM public.company WHERE id = :id",
                companyIdParameter);
        if (deleteStatus == 0) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void delete(String inn) {
        SqlParameterSource companyIdParameter = new MapSqlParameterSource().addValue("inn", inn);
        int deleteStatus = namedParameterJdbcTemplate.update("DELETE FROM public.company WHERE inn = :inn",
                companyIdParameter);
        if (deleteStatus == 0) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public Company save(Company company) {
        SqlParameterSource companyParametersForInsert = new MapSqlParameterSource()
                .addValue("name", company.getName())
                .addValue("inn", company.getInn())
                .addValue("phone_number", company.getPhoneNumber())
                .addValue("address", company.getAddress());
        namedParameterJdbcTemplate.update("INSERT INTO public.company (name, inn, phone_number, address) " +
                "VALUES (:name, :inn, :phone_number, :address)", companyParametersForInsert);
        return findByInn(company.getInn());
    }

    @Override
    public Company update(Company company) {
        SqlParameterSource companyParametersForUpdate = new MapSqlParameterSource()
                .addValue("id", company.getId())
                .addValue("name", company.getName())
                .addValue("inn", company.getInn())
                .addValue("phone_number", company.getPhoneNumber())
                .addValue("address", company.getAddress());
        namedParameterJdbcTemplate.update("UPDATE public.company SET (name, inn, phone_number, address) " +
                "VALUES (:name, :inn, :phone_number, :address) WHERE id = :id", companyParametersForUpdate);
        return findById(company.getId());
    }
}
