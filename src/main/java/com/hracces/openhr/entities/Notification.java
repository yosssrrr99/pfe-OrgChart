package com.hracces.openhr.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Notification {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
private Long id;
    private String message;
    private String idManger;
    private LocalDateTime date;
    private boolean isRead;

}
