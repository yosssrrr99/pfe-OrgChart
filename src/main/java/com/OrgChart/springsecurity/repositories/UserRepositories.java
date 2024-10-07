package com.OrgChart.springsecurity.repositories;

import com.OrgChart.springsecurity.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositories extends JpaRepository<User,Long> {

    boolean existsByMailAddress(String email);

    Optional<User> findByFirstName(String firstname);
    Optional<User> findByMailAddress(String mail);

}
