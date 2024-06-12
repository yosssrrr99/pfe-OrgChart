package com.hracces.openhr.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String idorg;

    private String nomorgL;

    private String nomorgS;
    private BigDecimal budgetGloabl;

}
