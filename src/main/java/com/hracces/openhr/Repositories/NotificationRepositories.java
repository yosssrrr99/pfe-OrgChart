package com.hracces.openhr.Repositories;

import com.hracces.openhr.entities.Notification;
import com.hracces.openhr.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepositories extends JpaRepository<Notification, Long> {

    List<Notification> findByIdMangerAndIsRead(String idManager, boolean isRead);


    @Query("SELECT COUNT(e.id) FROM Notification e WHERE e.idManger = :idManger AND e.isRead = false")
    int countfindMangerTypeStatus(@Param("idManger") String idManger);



}