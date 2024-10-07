package com.hracces.openhr.Services;

import org.springframework.kafka.annotation.KafkaListener;

import java.util.ArrayList;
import java.util.List;

public class KafkaConsumerService {
    private final List<String> messages = new ArrayList<>();

    @KafkaListener(topics = "${kafka.topic}", groupId = "group_id")
    public void listen(String message) {
        System.out.println("Received message: " + message);
        synchronized (messages) {
            messages.add(message);
        }
    }

    public List<String> getMessages() {
        synchronized (messages) {
            return new ArrayList<>(messages);
        }
    }
}
