package com.hracces.openhr.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
public class EmployeeOrg implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String tytrst;
    private String nameEmployee;
    private String idorg;
    private String nameManager;
    private int nbOrdre;
    private int nudoss;
    private String parentId;


}
