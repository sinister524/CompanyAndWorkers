package ru.aisa.companyAndWorkers.view;


import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.DateRenderer;
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
import java.util.NoSuchElementException;

@Title("Companies and workers")
public class MainView extends UI {

    private CompanyRepository companyRepository;
    private WorkerRepository workerRepository;

    private HorizontalLayout companyButtonsLayout;
    private Button createCompany;
    private Button editCompany;
    private Button deleteCompany;
    private HorizontalLayout workerButtonsLayout;
    private Button createWorker;
    private Button editWorker;
    private Button deleteWorker;

    private final TabSheet tabSheet = new TabSheet();
    private final VerticalLayout companyTab = new VerticalLayout();
    private final VerticalLayout workerTab = new VerticalLayout();
    private final Grid<Company> companyGrid = new Grid<>();
    private final Grid<Worker> workerGrid = new Grid<>();

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();

        initRepositories();
        initCompanyButtons();
        content.addComponent(companyButtonsLayout);
        content.setComponentAlignment(companyButtonsLayout, Alignment.MIDDLE_CENTER);
        initWorkerButtons();
        content.addComponent(workerButtonsLayout);
        content.setComponentAlignment(workerButtonsLayout, Alignment.MIDDLE_CENTER);

        initTabSheet();
        content.addComponent(tabSheet);
        content.setComponentAlignment(tabSheet, Alignment.MIDDLE_CENTER);

        setContent(content);
    }

    private void initRepositories(){
        SpringJdbcConfig jdbcConfig = new SpringJdbcConfig();
        DataSource dataSource = jdbcConfig.postgreDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        WorkerRowMapper workerRowMapper = new WorkerRowMapper();
        CompanyRowMapper companyRowMapper = new CompanyRowMapper();
        this.companyRepository = new CompanyDao(companyRowMapper, workerRowMapper, jdbcTemplate);
        this.workerRepository = new WorkerDao(workerRowMapper, jdbcTemplate, companyRepository);
    }

    private void initCompanyButtons(){
        this.companyButtonsLayout = new HorizontalLayout();

        this.createCompany = new Button("Создать компанию", clickEvent -> addWindow(
                new CreateOrEditCompanyForm(new Company(), companyGrid)));

        this.companyButtonsLayout.addComponent(createCompany);
    }

    private void initWorkerButtons(){
        this.workerButtonsLayout = new HorizontalLayout();

        this.createWorker = new Button("Создать работника", clickEvent -> addWindow(
                new CreateOrEditWorkerForm(new Worker(), workerGrid)));

        this.workerButtonsLayout.addComponent(createWorker);
        this.workerButtonsLayout.setVisible(false);
    }

    private void initTabSheet(){
        initCompanyGrid();
        this.companyTab.addComponent(companyGrid);
        this.companyTab.setComponentAlignment(companyGrid, Alignment.MIDDLE_CENTER);
        this.tabSheet.addTab(companyTab, "Компании");
        initWorkerGrid();
        this.workerTab.addComponent(workerGrid);
        this.workerTab.setComponentAlignment(workerGrid, Alignment.MIDDLE_CENTER);
        this.tabSheet.addTab(workerTab, "Работники");

        this.tabSheet.addSelectedTabChangeListener(selectedTabChangeEvent -> {
            Layout selectedTab = (Layout) selectedTabChangeEvent.getTabSheet().getSelectedTab();
            if (selectedTab.equals(companyTab)) {
                this.workerButtonsLayout.setVisible(false);
                this.companyButtonsLayout.setVisible(true);
            } else if (selectedTab.equals(workerTab)){
                this.companyButtonsLayout.setVisible(false);
                this.workerButtonsLayout.setVisible(true);
            }
        });
    }

    private void initCompanyGrid() {
        companyGrid.setItems(companyRepository.findAll());
        companyGrid.addColumn(Company::getName).setCaption("Название организации");
        companyGrid.addColumn(Company::getInn).setCaption("ИНН");
        companyGrid.addColumn(Company::getPhoneNumber).setCaption("Номер телефона");
        companyGrid.addColumn(Company::getAddress).setCaption("Адрес");
        companyGrid.setWidthFull();

        companyGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        companyGrid.addSelectionListener(selectionEvent -> {
            try {
                Company selectedCompany = selectionEvent.getFirstSelectedItem().orElseThrow(NoSuchElementException::new);
                if (editCompany != null){
                    this.companyButtonsLayout.removeComponent(editCompany);
                }
                this.editCompany = new Button("Редактировать компанию",
                        clickEvent -> this.addWindow(new CreateOrEditCompanyForm(selectedCompany, companyGrid)));
                this.companyButtonsLayout.addComponent(editCompany);

                if (deleteCompany != null){
                    this.companyButtonsLayout.removeComponent(deleteCompany);
                }
                this.deleteCompany = new Button("Удалить компанию", clickEvent -> {
                    companyRepository.delete(selectedCompany.getId());
                    companyGrid.deselectAll();
                    companyGrid.setItems(companyRepository.findAll());
                    workerGrid.setItems(workerRepository.findAll());
                    if (editCompany != null && deleteCompany != null){
                        this.companyButtonsLayout.removeComponent(editCompany);
                        this.companyButtonsLayout.removeComponent(deleteCompany);
                    }
                });
                this.companyButtonsLayout.addComponent(deleteCompany);
                workerGrid.setItems(workerRepository.findAllCompanyWorkers(selectedCompany));
            } catch (NoSuchElementException e) {
                companyGrid.deselectAll();
                workerGrid.setItems(workerRepository.findAll());
                if (editCompany != null && deleteCompany != null){
                    this.companyButtonsLayout.removeComponent(editCompany);
                    this.companyButtonsLayout.removeComponent(deleteCompany);
                }
            }
        });
    }

    private void initWorkerGrid() {
        workerGrid.setItems(workerRepository.findAll());
        workerGrid.addColumn(Worker::getName).setCaption("Имя");
        workerGrid.addColumn(Worker::getBirthday, new DateRenderer("%1$td.%1$tm.%1$tY")).setCaption("Дата рождения");
        workerGrid.addColumn(Worker::getEmail).setCaption("Email");
        workerGrid.addColumn(worker -> worker.getCompany().getName()).setCaption("Компания");
        workerGrid.setWidthFull();
        workerGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        workerGrid.addSelectionListener(selectionEvent -> {
            try {
                Worker selectedWorker = selectionEvent.getFirstSelectedItem().orElseThrow(NoSuchElementException::new);
                if (editWorker != null){
                    workerButtonsLayout.removeComponent(editWorker);
                }
                this.editWorker = new Button("Редактировать работника", clickEvent -> addWindow(
                        new CreateOrEditWorkerForm(selectedWorker, workerGrid)));
                this.workerButtonsLayout.addComponent(editWorker);

                if (deleteWorker != null){
                    workerButtonsLayout.removeComponent(deleteWorker);
                }
                this.deleteWorker = new Button("Удалить работника", clickEvent -> {
                    workerRepository.delete(selectedWorker.getId());
                    workerGrid.deselectAll();
                    workerGrid.setItems(workerRepository.findAll());
                    if(editWorker!= null && deleteWorker != null){
                        this.workerButtonsLayout.removeComponent(editWorker);
                        this.workerButtonsLayout.removeComponent(deleteWorker);
                    }
                });
                this.workerButtonsLayout.addComponent(deleteWorker);
            } catch (NoSuchElementException e) {
                workerGrid.deselectAll();
                if(editWorker!= null && deleteWorker != null){
                    this.workerButtonsLayout.removeComponent(editWorker);
                    this.workerButtonsLayout.removeComponent(deleteWorker);
                }
            }

        });
    }

    @WebServlet(urlPatterns = "/*", name = "MainViewServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MainView.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }

}
