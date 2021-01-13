package ru.aisa.companyAndWorkers.dao;

import junit.framework.TestCase;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.aisa.companyAndWorkers.comfig.SpringJdbcConfig;
import ru.aisa.companyAndWorkers.entity.Company;
import ru.aisa.companyAndWorkers.entity.Worker;
import ru.aisa.companyAndWorkers.row_mapper.CompanyRowMapper;
import ru.aisa.companyAndWorkers.row_mapper.WorkerRowMapper;

import javax.sql.DataSource;
import java.io.File;

public class DaoTest extends TestCase {

    private WorkerDao workerDao;
    private CompanyDao companyDao;
    private WorkerRowMapper workerRowMapper;
    private CompanyRowMapper companyRowMapper;
    private SpringJdbcConfig jdbcConfig;
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public void setUp() throws Exception {
        this.jdbcConfig = new SpringJdbcConfig();
        this.dataSource = jdbcConfig.postgreDataSource();
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.workerRowMapper = new WorkerRowMapper();
        this.companyRowMapper = new CompanyRowMapper();
        this.companyDao = new CompanyDao(companyRowMapper, workerRowMapper, jdbcTemplate);
        this.workerDao = new WorkerDao(workerRowMapper, jdbcTemplate, companyDao);
    }

    public void tearDown() throws Exception {
        workerDao.clear();
        companyDao.clear();
    }

    public void testFindAll() {
        Company company = new Company("IBM", "1111111", "844898745", "USA");
        company = companyDao.save(company);
        assertNotNull(company);
        System.out.println(company);
        Worker worker = new Worker("Jonie", "1983", "some@mail.com", new File("photo.jpeg"), company);
        worker = workerDao.save(worker);
        System.out.println(worker);
        assertNotNull(worker);
        Worker worker2 = new Worker("Bob", "1993", "kotik@mail.com", new File("photo.jpeg"), company);
        worker2 = workerDao.save(worker2);
        assertNotNull(worker2);
        System.out.println(worker2);
        System.out.println(companyDao.findById(worker2.getCompany().getId()));
        System.out.println();

        System.out.println(companyDao.findAll());
        System.out.println();
        System.out.println(workerDao.findAll());
    }

    public void testDelete() {
        Company company = new Company("IBM", "1111111", "844898745", "USA");
        company = companyDao.save(company);
        assertNotNull(company);
        Worker worker = new Worker("Jonie", "1983", "some@mail.com", new File("photo.jpeg"), company);
        worker = workerDao.save(worker);
        assertNotNull(worker);
        Worker worker2 = new Worker("Bob", "1993", "kotik@mail.com", new File("photo.jpeg"), company);
        worker2 = workerDao.save(worker2);
        assertNotNull(worker2);

        System.out.println(companyDao.findAll());
        System.out.println();
        System.out.println(workerDao.findAll());
        System.out.println();

        companyDao.delete(company.getId());

        System.out.println(companyDao.findAll());
        System.out.println();
        System.out.println(workerDao.findAll());
    }

    public void testSave() {
        Company company = new Company("IBM", "1111111", "844898745", "USA");
        company = companyDao.save(company);
        assertNotNull(company);
        System.out.println(company);
        Worker worker = new Worker("Jonie", "1983", "some@mail.com", new File("photo.jpeg"), company);
        worker = workerDao.save(worker);
        System.out.println(worker);
        assertNotNull(worker);
        Worker worker2 = new Worker("Bob", "1993", "kotik@mail.com", new File("photo.jpeg"), company);
        worker2 = workerDao.save(worker2);
        assertNotNull(worker2);
        System.out.println(worker2);
        System.out.println(companyDao.findById(worker2.getCompany().getId()));
    }

    public void testUpdate() {
        Company company = new Company("IBM", "1111111", "844898745", "USA");
        company = companyDao.save(company);
        assertNotNull(company);
        System.out.println(company);
        Worker worker = new Worker("Jonie", "1983", "some@mail.com", new File("photo.jpeg"), company);
        worker = workerDao.save(worker);
        System.out.println(worker);
        assertNotNull(worker);
        Worker worker2 = new Worker("Bob", "1993", "kotik@mail.com", new File("photo.jpeg"), company);
        worker2 = workerDao.save(worker2);
        assertNotNull(worker2);
        System.out.println(worker2);
        System.out.println();
        System.out.println(companyDao.findById(worker2.getCompany().getId()));
        System.out.println();

        company.setName("Apple");

        Long companyId = company.getId();
        company = companyDao.update(company);
        assertEquals(companyId, company.getId());
        assertEquals("Apple", company.getName());
        System.out.println(company);
        System.out.println();

        worker.setName("John");
        worker = workerDao.update(worker);
        assertEquals("John", worker.getName());
        System.out.println(worker);
    }
}