package ru.aisa.companyAndWorkers.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

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

    @Override
    public String toString() {
        return name + ", id = " + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return id.equals(company.id) && name.equals(company.name) && inn.equals(company.inn) && phoneNumber.equals(company.phoneNumber) && address.equals(company.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, inn, phoneNumber, address);
    }
}
