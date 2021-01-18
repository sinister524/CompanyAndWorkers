package ru.aisa.companyAndWorkers.view;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.*;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.aisa.companyAndWorkers.comfig.SpringJdbcConfig;
import ru.aisa.companyAndWorkers.dao.CompanyDao;
import ru.aisa.companyAndWorkers.dao.WorkerDao;
import ru.aisa.companyAndWorkers.entity.Company;
import ru.aisa.companyAndWorkers.entity.Worker;
import ru.aisa.companyAndWorkers.exception.ValueNotUniqueException;
import ru.aisa.companyAndWorkers.repository.CompanyRepository;
import ru.aisa.companyAndWorkers.repository.WorkerRepository;
import ru.aisa.companyAndWorkers.row_mapper.CompanyRowMapper;
import ru.aisa.companyAndWorkers.row_mapper.WorkerRowMapper;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.NoSuchElementException;

public class CreateOrEditWorkerForm extends Window {
    private WorkerRepository workerRepository;
    private CompanyRepository companyRepository;

    private final VerticalLayout windowLayout;
    private final HorizontalLayout nameLayout;
    private final HorizontalLayout birthdayLayout;
    private final HorizontalLayout emailLayout;
    private final HorizontalLayout companyLayout;
    private final HorizontalLayout buttonLayout;

    private final Worker worker;
    private final Binder<Worker> binder;

    private final Grid<Worker> workerGrid;

    public CreateOrEditWorkerForm(Worker worker, Grid<Worker> workerGrid) {
        this.worker = worker;
        this.workerGrid = workerGrid;
        initRepository();
        this.windowLayout = new VerticalLayout();
        this.nameLayout = new HorizontalLayout();
        this.birthdayLayout = new HorizontalLayout();
        this.emailLayout = new HorizontalLayout();
        this.companyLayout = new HorizontalLayout();
        this.buttonLayout = new HorizontalLayout();

        this.binder = new Binder<>();
        initNameLayout();
        initBirthdayLayout();
        initEmailLayout();
        initCompanyLayout();
        initButtonLayout();
        initWindowLayout();
        this.binder.readBean(worker);
        this.setModal(true);
    }

    private void initRepository(){
        SpringJdbcConfig jdbcConfig = new SpringJdbcConfig();
        DataSource dataSource = jdbcConfig.postgreDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        WorkerRowMapper workerRowMapper = new WorkerRowMapper();
        CompanyRowMapper companyRowMapper = new CompanyRowMapper();
        this.companyRepository = new CompanyDao(companyRowMapper, workerRowMapper, jdbcTemplate);
        this.workerRepository = new WorkerDao(workerRowMapper, jdbcTemplate, companyRepository);
    }

    private void initNameLayout(){
        Label nameLabel = new Label("Имя");
        this.nameLayout.addComponent(nameLabel);
        TextField nameField = new TextField();
        nameField.setPlaceholder("Имя работника");
        this.binder.forField(nameField)
                .asRequired("Поле должно быть заполненно")
                .bind(Worker::getName, Worker::setName);
        this.nameLayout.addComponent(nameField);
    }

    private void initBirthdayLayout(){
        Label innLabel = new Label("Дата рождения");
        this.birthdayLayout.addComponent(innLabel);
        DateField birthdayField = new DateField();
        birthdayField.setDateFormat("dd.MM.yyyy");
        this.binder.forField(birthdayField)
                .asRequired()
                .withValidator(new DateRangeValidator("Дата рождения не может быть раньше 1900 года" +
                        " и позже чем за 16 лет от сегодня", LocalDate.of(1900, 1, 1),
                        LocalDate.now().minusYears(16)))
                .bind(Worker::getBirthdayInLocalDate, Worker::setBirthdayFromLocalDate);
        this.birthdayLayout.addComponent(birthdayField);
    }

    private void initEmailLayout(){
        Label emailLabel = new Label("Телефон");
        this.emailLayout.addComponent(emailLabel);
        TextField emailField = new TextField();
        emailField.setPlaceholder("example@domain.com");
        this.binder.forField(emailField)
                .withValidator(new EmailValidator("Строка не похожа на Email"))
                .bind(Worker::getEmail, Worker::setEmail);
        this.emailLayout.addComponent(emailField);
    }

    private void initCompanyLayout(){
        Label companyLabel = new Label("Компания");
        this.companyLayout.addComponent(companyLabel);
        ComboBox<Company> companyComboBox = new ComboBox<>();
        companyComboBox.setItems(companyRepository.findAll());
        this.binder.forField(companyComboBox)
                .asRequired()
                .bind(Worker::getCompany, Worker::setCompany);
        this.companyLayout.addComponent(companyComboBox);
    }

    private void initButtonLayout(){
        Button createButton = new Button("Сохранить", clickEvent -> {
            if (this.worker.getId() == null){
                try {
                    this.binder.writeBean(worker);
                    this.workerRepository.save(worker);
                    this.workerGrid.setItems(workerRepository.findAll());
                } catch (ValidationException e) {
                    Notification.show("Создание объекта не удалось", Notification.Type.ERROR_MESSAGE);
                } catch (ValueNotUniqueException e) {
                    Notification.show("Работник с таким Email уже существует", Notification.Type.ERROR_MESSAGE);
                }
            } else {
                try {
                    this.binder.writeBean(worker);
                    this.workerRepository.update(worker);
                    this.workerGrid.deselectAll();
                    this.workerGrid.setItems(workerRepository.findAll());
                } catch (ValidationException e) {
                    Notification.show("Редактирование объекта не удалось", Notification.Type.ERROR_MESSAGE);
                } catch (NoSuchElementException e) {
                    Notification.show("Редактируемого элемента не существует", Notification.Type.ERROR_MESSAGE);
                } catch (ValueNotUniqueException e) {
                    Notification.show("Работник с таким Email уже существует", Notification.Type.ERROR_MESSAGE);
                }
            }
            close();
        });
        Button cancelButton = new Button("Отмена", clickEvent -> close());
        this.buttonLayout.addComponents(createButton, cancelButton);
    }

    private void initWindowLayout(){
        this.windowLayout.addComponent(nameLayout);
        this.windowLayout.addComponent(birthdayLayout);
        this.windowLayout.addComponent(emailLayout);
        this.windowLayout.addComponent(companyLayout);
        this.windowLayout.addComponent(buttonLayout);
        this.setContent(windowLayout);
    }
}
