package ru.aisa.companyAndWorkers.repository;

import ru.aisa.companyAndWorkers.entity.Worker;

import java.util.List;
import java.util.Optional;

public interface WorkerRepository {

    List<Worker> findAll();

    Worker findById (Long id);

    Worker findByEmail(String email);

    void delete (Long id);

    void delete (String email);

    void clear();

    Worker save (Worker worker);

    Worker update (Worker worker);
}
