package ru.aisa.companyAndWorkers.repository;

import ru.aisa.companyAndWorkers.entity.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository {

    List<Company> findAll();

    Company findById (Long id);

    Company findByInn (String inn);

    void delete (Long id);

    void delete (String inn);

    void clear ();

    Company save (Company company);

    Company update (Company company);
}
