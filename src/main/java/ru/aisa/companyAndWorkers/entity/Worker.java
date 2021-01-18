package ru.aisa.companyAndWorkers.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Worker implements Comparable<Worker>{
    private Long id;
    private String name;
    private Date birthday;
    private String email;
    private Company company;

    public Worker(Long id, String name, Date birthday, String email) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.email = email;
    }

    public Worker(String name, Date birthday, String email, Company company) {
        this.name = name;
        this.birthday = birthday;
        this.email = email;
        this.company = company;
    }

    public LocalDate getBirthdayInLocalDate (){
        if (birthday == null) {
            return LocalDate.now().minusYears(16);
        }
        return Instant.ofEpochMilli(birthday.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void setBirthdayFromLocalDate(LocalDate localBirthday){
        this.birthday = Date.from(localBirthday.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    @Override
    public String toString() {
        return "Worker{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthday='" + birthday + '\'' +
                ", email='" + email + '\'' +
                ", company=" + company.getName() +
                '}';
    }

    @Override
    public int compareTo(Worker worker) {
        return id.compareTo(worker.getId());
    }
}
