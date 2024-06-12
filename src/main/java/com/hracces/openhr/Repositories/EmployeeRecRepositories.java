package com.hracces.openhr.Repositories;

import com.hracces.openhr.entities.EmployeeRec;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRecRepositories extends JpaRepository<EmployeeRec,Integer> {

    List<EmployeeRec> findAllByIdorg(String idorg);
    EmployeeRec findByIdorg(String id);
}
