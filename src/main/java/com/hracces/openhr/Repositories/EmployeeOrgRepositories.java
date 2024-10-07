package com.hracces.openhr.Repositories;

import com.hracces.openhr.entities.EmployeeOrg;
import org.mortbay.html.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeOrgRepositories extends JpaRepository<EmployeeOrg,Integer> {

    List<EmployeeOrg> findAllByOrderByNbOrdreAsc();


    @Query("SELECT eo FROM EmployeeOrg eo WHERE eo.parentId = :parentId ORDER BY eo.nbOrdre ASC")
    List<EmployeeOrg> findByParentIdOrderByNbOrdreAsc(@Param("parentId") String parentId);

    List<EmployeeOrg> findByParentIdAndNbOrdre(String parentId, int nbOrdre);



    @Query("SELECT o FROM EmployeeOrg o WHERE o.nbOrdre IN :levels")
    List<EmployeeOrg> findAllByLevels(@Param("levels") List<Integer> levels);


}
