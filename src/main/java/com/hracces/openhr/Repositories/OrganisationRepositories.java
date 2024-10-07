package com.hracces.openhr.Repositories;

import com.hracces.openhr.entities.EmployeeData;
import com.hracces.openhr.entities.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrganisationRepositories extends JpaRepository<com.hracces.openhr.entities.Organisation, Long> {

    @Query("select o from Organisation o ")
    List<Organisation> findOrg();
}
