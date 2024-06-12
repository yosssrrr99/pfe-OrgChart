package com.hracces.openhr.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import java.io.Serializable;
import java.util.Date;


@Getter
@Setter
@Entity
public class EmployeeData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String matcle;

    private double mtsal;

    @Column(length = 10000)
    private String blobData;

    private String nomuse;

    private String prenom;

    private String idorg;

    private String nomorg;

    private String poste;
    private double budget;
    private Date dateE;
    private Date dateS;







}
