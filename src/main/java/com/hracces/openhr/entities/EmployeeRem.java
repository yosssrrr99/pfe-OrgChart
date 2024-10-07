package com.hracces.openhr.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;


@Getter
@Setter
@Entity
public class EmployeeRem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String nom;
    private double mtsal;
    private String poste;
    private String idorg;
    private boolean status;
    private LocalDate date;
    private double budgetGloabl;
    private double gab;
    @Enumerated(EnumType.STRING)
    private Status TypeStatus;
    private String idManger;
    private double budgetAnnuel;
    private double pourcentage;



}