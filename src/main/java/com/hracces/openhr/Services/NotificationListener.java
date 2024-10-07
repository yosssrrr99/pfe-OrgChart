package com.hracces.openhr.Services;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationListener {

        @KafkaListener(topics = "notifications", groupId = "group_id")
        public void listen(String message) {
            System.out.println("Received notification: " + message);
            // Traitez le message reçu ici
        }
    }


