package com.hracces.openhr.Repositories;


import com.hracces.openhr.entities.Employee;
import com.hracces.openhr.entities.EmployeeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepositories extends JpaRepository<EmployeeData, Long> {


    @Query("select e from EmployeeData e ")
    List<EmployeeData> findEmp();


    @Query("SELECT ed.nomorg FROM EmployeeData ed WHERE ed.idorg = :idOrg")
    List<String> findNameById(@Param("idOrg") String idOrg);

    @Query("select sum(e.mtsal) from EmployeeData  e where e.idorg = :idOrg")
    Double BudgetAnnuelByDep(@Param("idOrg") String idOrg);

}

