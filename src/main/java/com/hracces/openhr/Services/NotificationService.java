package com.hracces.openhr.Services;


import com.hracces.openhr.Repositories.EmployeeOrgRepositories;
import com.hracces.openhr.Repositories.EmployeeRecRepositories;
import com.hracces.openhr.Repositories.EmployeeRemRepositories;
import com.hracces.openhr.Repositories.NotificationRepositories;
import com.hracces.openhr.entities.EmployeeRec;
import com.hracces.openhr.entities.EmployeeRem;
import com.hracces.openhr.entities.Notification;
import com.hracces.openhr.entities.Status;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepositories notificationRepositories;
    private final EmployeeRecRepositories employeeRecRepositories;
    private final EmployeeOrgRepositories employeeOrgRepositories;
    private final EmployeeRemRepositories employeeRemRepositories;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;




    public void sendNotificationToManager(String managerId, String message) {
        Notification notification = new Notification();
        notification.setIdManger(managerId);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setDate(LocalDateTime.now());
        notificationRepositories.save(notification);
    }
    public void updateStatusApp(String managerId) {
        List<EmployeeRec> employeeRecs = employeeRecRepositories.findAllByIdManger(managerId);


        for (int i = 0; i < employeeRecs.size(); i++) {
            EmployeeRec emp = employeeRecs.get(i);
            emp.setTypeStatus(Status.Approuver);
            emp.setStatus(true);
            employeeRecRepositories.save(emp);
        }


        List<EmployeeRec> emp=employeeRecRepositories.findAllByIdManger("123456");

        // Create the message payload (you can format it as needed)
        String message = String.format("La demande  de recrutement de le manger : %s, with Budget: %.2f est acceptée", emp.get(0).getIdManger(), emp.get(0).getBudgetGlobal());

        // Send the message to Kafka
        kafkaTemplate.send("statusRec", message);
    }

    public void updateStatusAppRem(String managerId) {
        List<EmployeeRem> employeeRecs = employeeRemRepositories.findAllByIdManger(managerId);


        for (int i = 0; i < employeeRecs.size(); i++) {
            EmployeeRem emp = employeeRecs.get(i);
            emp.setTypeStatus(Status.Approuver);
            emp.setStatus(true);
            employeeRemRepositories.save(emp);
        }


        List<EmployeeRem> emp=employeeRemRepositories.findAllByIdManger("123456");

        // Create the message payload (you can format it as needed)
        String message = String.format("La demande de renumeration de le manger : %s, with Budget: %.2f est accepté", emp.get(0).getIdManger(), emp.get(0).getBudgetGloabl());

        // Send the message to Kafka
        kafkaTemplate.send("statusRemm", message);
    }
    public void updateStatusRef(String managerId) {
        List<EmployeeRec> employeeRecs = employeeRecRepositories.findAllByIdManger(managerId);


        for (int i = 0; i < employeeRecs.size(); i++) {
            EmployeeRec emp = employeeRecs.get(i);
            emp.setTypeStatus(Status.Refuser);
            emp.setStatus(true);
            employeeRecRepositories.save(emp);

        }

        List<EmployeeRec> emp=employeeRecRepositories.findAllByIdManger("123456");

        // Create the message payload (you can format it as needed)
        String message = String.format("La demande de  recrutement le manger : %s, with Budget: %.2f est refusée", emp.get(0).getIdManger(), emp.get(0).getBudgetGlobal());

        // Send the message to Kafka
        kafkaTemplate.send("statusRec", message);
    }

    public void updateStatusRefRem(String managerId) {
        List<EmployeeRem> employeeRecs = employeeRemRepositories.findAllByIdManger(managerId);


        for (int i = 0; i < employeeRecs.size(); i++) {
            EmployeeRem emp = employeeRecs.get(i);
            emp.setTypeStatus(Status.Refuser);
            emp.setStatus(true);
            employeeRemRepositories.save(emp);

        }

        List<EmployeeRem> emp=employeeRemRepositories.findAllByIdManger("123456");

        // Create the message payload (you can format it as needed)
        String message = String.format("La demande de renumerartion le manger : %s, with Budget: %.2f est refusée", emp.get(0).getIdManger(), emp.get(0).getBudgetGloabl());

        // Send the message to Kafka
        kafkaTemplate.send("statusRemm", message);
    }

    public List<Notification> getNotificationsByManagerId(String managerId) {
        return notificationRepositories.findByIdMangerAndIsRead(managerId,false);
    }


    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepositories.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + notificationId));

        notification.setRead(true);
        notificationRepositories.save(notification);
    }


    public void sendNotification(String message) {
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }
    public int nbNotif(String idManager){
        return notificationRepositories.countfindMangerTypeStatus(idManager);
    }
}






