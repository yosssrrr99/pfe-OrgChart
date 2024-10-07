package com.hracces.openhr.Repositories;

import com.hracces.openhr.entities.EmployeeRec;
import com.hracces.openhr.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Repository
public interface EmployeeRecRepositories extends JpaRepository<EmployeeRec,Integer> {

    List<EmployeeRec> findAllByIdorg(String idorg);


    @Query("SELECT e FROM EmployeeRec e WHERE e.idManger = :idManager AND e.TypeStatus = :status")
    List<EmployeeRec> findByIdManagerAndStatus(@Param("idManager") String idManager, @Param("status") Status status);
    List<EmployeeRec>  findAllByIdManger(String id);
    @Query("SELECT e FROM EmployeeRec e WHERE e.idManger = :idManager  AND e.date = (SELECT MAX(e2.date) FROM EmployeeRec e2 WHERE e2.idManger = :idManager)")
    List<EmployeeRec> findByIdManagerAndStatusWithLatestDate(@Param("idManager") String idManager);

    @Query("SELECT distinct e.idManger FROM EmployeeRec e WHERE e.TypeStatus = :status")
    List<String> findMangerTypeStatus( @Param("status") Status status);
    @Query("SELECT count (distinct e.idManger) FROM EmployeeRec e WHERE e.TypeStatus = :status")
    int countfindMangerTypeStatus( @Param("status") Status status);
}
