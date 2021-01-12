package ru.aisa.companyAndWorkers.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    private Long id;
    private String name;
    private String inn;
    private String phoneNumber;
    private String address;
    private List<Worker> workers;

    public Company(String name, String inn, String phoneNumber, String address) {
        this.name = name;
        this.inn = inn;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public Company(Long id, String name, String inn, String phoneNumber, String address) {
        this.id = id;
        this.name = name;
        this.inn = inn;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
