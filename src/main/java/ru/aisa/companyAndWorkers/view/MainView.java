package ru.aisa.companyAndWorkers.view;


import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.aisa.companyAndWorkers.comfig.SpringJdbcConfig;
import ru.aisa.companyAndWorkers.dao.CompanyDao;
import ru.aisa.companyAndWorkers.dao.WorkerDao;
import ru.aisa.companyAndWorkers.entity.Company;
import ru.aisa.companyAndWorkers.entity.Worker;
import ru.aisa.companyAndWorkers.repository.CompanyRepository;
import ru.aisa.companyAndWorkers.repository.WorkerRepository;
import ru.aisa.companyAndWorkers.row_mapper.CompanyRowMapper;
import ru.aisa.companyAndWorkers.row_mapper.WorkerRowMapper;

import javax.servlet.annotation.WebServlet;
import javax.sql.DataSource;

@Title("main")
public class MainView extends UI {

    private final Grid<Company> companyGrid = new Grid<>();
    private final Grid<Worker> workerGrid = new Grid<>();
    private final VerticalLayout companyTab = new VerticalLayout();
    private final VerticalLayout workerTab = new VerticalLayout();


    private CompanyRepository companyRepository;
    private WorkerRepository workerRepository;

    @Override
    protected void init(VaadinRequest request) {
        initDao();
        initCompanyGrid();
        VerticalLayout content = new VerticalLayout();


        Button createButton = new Button("Создать", clickEvent -> addWindow(
                new CreateOrEditCompanyForm(new Company(), companyGrid)));
        content.addComponent(createButton);
        content.addComponent(companyGrid);
        content.setComponentAlignment(companyGrid, Alignment.MIDDLE_CENTER);



        content.setComponentAlignment(createButton, Alignment.MIDDLE_CENTER);


        setContent(content);
    }

    private void initDao(){
        SpringJdbcConfig jdbcConfig = new SpringJdbcConfig();
        DataSource dataSource = jdbcConfig.postgreDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        WorkerRowMapper workerRowMapper = new WorkerRowMapper();
        CompanyRowMapper companyRowMapper = new CompanyRowMapper();
        this.companyRepository = new CompanyDao(companyRowMapper, workerRowMapper, jdbcTemplate);
        this.workerRepository = new WorkerDao(workerRowMapper, jdbcTemplate, companyRepository);
    }


    private void initCompanyGrid() {
        companyGrid.setItems(companyRepository.findAll());
        companyGrid.addColumn(Company::getName).setCaption("Название организации");
        companyGrid.addColumn(Company::getInn).setCaption("ИНН");
        companyGrid.addColumn(Company::getPhoneNumber).setCaption("Номер телефона");
        companyGrid.addColumn(Company::getAddress).setCaption("Адрес");
        companyGrid.setWidthFull();
    }

    @WebServlet(urlPatterns = "/*", name = "MainViewServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MainView.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }

}
