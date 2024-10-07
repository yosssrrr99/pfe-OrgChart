package com.hracces.openhr.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
public class EmployeeRec implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int number;
    private String classification;
    private double minbudget;
    private double maxbudget;
    private String idorg;
    private boolean status;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private Status TypeStatus;
    private String idManger;
    private double budgetGlobal;
    private double gab;


}
