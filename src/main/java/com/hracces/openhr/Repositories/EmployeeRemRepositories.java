package com.hracces.openhr.Repositories;

import com.hracces.openhr.entities.EmployeeRec;
import com.hracces.openhr.entities.EmployeeRem;
import com.hracces.openhr.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRemRepositories extends JpaRepository<EmployeeRem,Long> {

    List<EmployeeRem> findAllByIdManger(String id);

    List<EmployeeRem> findAllByIdorg(String idorg);
    @Query("SELECT e FROM EmployeeRem e WHERE e.idManger = :idManager  AND e.date = (SELECT MAX(e2.date) FROM EmployeeRem e2 WHERE e2.idManger = :idManager)")
    List<EmployeeRem> findByIdManagerRemAndStatusWithLatestDate(@Param("idManager") String idManager);

    @Query("SELECT count (distinct e.idManger) FROM EmployeeRem e WHERE e.TypeStatus = :status")
    int countfindMangerTypeStatus( @Param("status") Status status);



    @Query("SELECT e FROM EmployeeRem e WHERE e.idManger = :idManager And e.TypeStatus=:status  AND e.date = (SELECT MAX(e2.date) FROM EmployeeRem e2 WHERE e2.idManger = :idManager)")
    List<EmployeeRem> getdemandeRem(@Param("idManager") String idManager,@Param("status") Status status);
}
