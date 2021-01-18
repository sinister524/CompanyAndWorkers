package ru.aisa.companyAndWorkers.view;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.ui.*;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.aisa.companyAndWorkers.comfig.SpringJdbcConfig;
import ru.aisa.companyAndWorkers.dao.CompanyDao;
import ru.aisa.companyAndWorkers.entity.Company;
import ru.aisa.companyAndWorkers.exception.ValueNotUniqueException;
import ru.aisa.companyAndWorkers.repository.CompanyRepository;
import ru.aisa.companyAndWorkers.row_mapper.CompanyRowMapper;
import ru.aisa.companyAndWorkers.row_mapper.WorkerRowMapper;

import javax.sql.DataSource;
import java.util.NoSuchElementException;

public class CreateOrEditCompanyForm extends Window {
    private CompanyRepository repository;

    private final VerticalLayout windowLayout;
    private final HorizontalLayout nameLayout;
    private final HorizontalLayout innLayout;
    private final HorizontalLayout phoneLayout;
    private final HorizontalLayout addressLayout;
    private final HorizontalLayout buttonLayout;

    private final Company company;
    private final Binder<Company> binder;

    private final Grid<Company> companyGrid;

    public CreateOrEditCompanyForm(Company company, Grid<Company> companyGrid) {
        this.company = company;
        this.companyGrid = companyGrid;
        initRepository();
        this.windowLayout = new VerticalLayout();
        this.nameLayout = new HorizontalLayout();
        this.innLayout = new HorizontalLayout();
        this.phoneLayout = new HorizontalLayout();
        this.addressLayout = new HorizontalLayout();
        this.buttonLayout = new HorizontalLayout();

        this.binder = new Binder<>();
        initNameLayout();
        initInnLayout();
        initPhoneLayout();
        initAddressLayout();
        initButtonLayout();
        initWindowLayout();
        this.binder.readBean(company);
        this.setModal(true);
    }

    private void initRepository(){
        SpringJdbcConfig jdbcConfig = new SpringJdbcConfig();
        DataSource dataSource = jdbcConfig.postgreDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        WorkerRowMapper workerRowMapper = new WorkerRowMapper();
        CompanyRowMapper companyRowMapper = new CompanyRowMapper();
        this.repository = new CompanyDao(companyRowMapper, workerRowMapper, jdbcTemplate);
    }

    private void initNameLayout(){
        Label nameLabel = new Label("Название");
        this.nameLayout.addComponent(nameLabel);
        TextField nameField = new TextField();
        nameField.setPlaceholder("Название компании");
        this.binder.forField(nameField)
                .asRequired("Поле должно быть заполненно")
                .bind(Company::getName, Company::setName);
        this.nameLayout.addComponent(nameField);
    }

    private void initInnLayout(){
        Label innLabel = new Label("ИНН");
        this.innLayout.addComponent(innLabel);
        TextField innField = new TextField();
        innField.setPlaceholder("ИНН компании");
        this.binder.forField(innField)
                .withValidator(inn -> inn.length() == 10, "ИНН должен состоять из 10 цифр")
                .withValidator(inn -> inn.matches("-?\\d+(\\.\\d+)?"), "ИНН должен содержать только цифры")
                .bind(Company::getInn, Company::setInn);
        this.innLayout.addComponent(innField);
    }

    private void initPhoneLayout(){
        Label phoneLabel = new Label("Телефон");
        this.phoneLayout.addComponent(phoneLabel);
        TextField phoneField = new TextField();
        phoneField.setPlaceholder("+79991112233");
        phoneField.setValue("+7");
        this.binder.forField(phoneField)
                .withValidator(phoneNumber -> phoneNumber.length() == 12,
                        "Телефонный номер должен состоять из 12 символов")
                .withValidator(phoneNumber -> phoneNumber.startsWith("+7")
                                && phoneNumber.substring(1, 11).matches("-?\\d+(\\.\\d+)?"),
                        "Номер телефона должен начиаться с +7 и содержать только цифры")
                .bind(Company::getPhoneNumber, Company::setPhoneNumber);
        this.phoneLayout.addComponent(phoneField);
    }

    private void initAddressLayout(){
        Label addressLabel = new Label("Адресс");
        this.addressLayout.addComponent(addressLabel);
        TextField addressField = new TextField();
        addressField.setPlaceholder("Адресс компании");
        this. binder.forField(addressField)
                .asRequired("Поле должно быть заполненно")
                .bind(Company::getAddress, Company::setAddress);
        this. addressLayout.addComponent(addressField);
    }

    private void initButtonLayout(){
        Button createButton = new Button("Сохранить", clickEvent -> {
            if (this.company.getId() == null){
                try {
                    this.binder.writeBean(company);
                    this.repository.save(company);
                    this.companyGrid.setItems(repository.findAll());
                } catch (ValidationException e) {
                    Notification.show("Создание объекта не удалось", Notification.Type.ERROR_MESSAGE);
                } catch (ValueNotUniqueException e) {
                    Notification.show("Компания с таким ИНН уже существует", Notification.Type.ERROR_MESSAGE);
                }
            } else {
                try {
                    this.binder.writeBean(company);
                    this.repository.update(company);
                    this.companyGrid.deselectAll();
                    this.companyGrid.setItems(repository.findAll());
                } catch (ValidationException e) {
                    Notification.show("Редактирование объекта не удалось", Notification.Type.ERROR_MESSAGE);
                } catch (NoSuchElementException e) {
                    Notification.show("Редактируемого элемента не существует", Notification.Type.ERROR_MESSAGE);
                } catch (ValueNotUniqueException e) {
                    Notification.show("Компания с таким ИНН уже существует", Notification.Type.ERROR_MESSAGE);
                }
            }
            close();
        });
        Button cancelButton = new Button("Отмена", clickEvent -> close());
        this.buttonLayout.addComponents(createButton, cancelButton);
    }

    private void initWindowLayout(){
        this.windowLayout.addComponent(nameLayout);
        this.windowLayout.addComponent(innLayout);
        this.windowLayout.addComponent(phoneLayout);
        this.windowLayout.addComponent(addressLayout);
        this.windowLayout.addComponent(buttonLayout);
        this.setContent(windowLayout);
    }
}
