package ru.aisa.companyAndWorkers.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Worker implements Comparable<Worker>{
    private Long id;
    private String name;
    private Date birthday;
    private String email;
    private File photo;
    private Company company;

    public Worker(Long id, String name, Date birthday, String email, File photo) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.email = email;
        this.photo = photo;
    }

    public Worker(String name, Date birthday, String email, File photo, Company company) {
        this.name = name;
        this.birthday = birthday;
        this.email = email;
        this.photo = photo;
        this.company = company;
    }

    public Worker(String name, Date birthday, String email, Company company) {
        this.name = name;
        this.birthday = birthday;
        this.email = email;
        this.company = company;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthday='" + birthday + '\'' +
                ", email='" + email + '\'' +
                ", photo=" + photo +
                ", company=" + company.getName() +
                '}';
    }

    @Override
    public int compareTo(Worker worker) {
        return id.compareTo(worker.getId());
    }
}
