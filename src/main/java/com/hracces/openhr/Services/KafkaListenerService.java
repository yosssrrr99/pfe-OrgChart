package com.hracces.openhr.Services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class KafkaListenerService {
    private final ConcurrentLinkedQueue<String> messagesQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> statusRecQueue = new ConcurrentLinkedQueue<>();


    //demande rem

    private final ConcurrentLinkedQueue<String> statusRemQueue = new ConcurrentLinkedQueue<>();

//status refuser ou refuser
    private final ConcurrentLinkedQueue<String> statusRemmQueue = new ConcurrentLinkedQueue<>();

    @KafkaListener(topics = "notifications", groupId = "com.example")
    public void listen(String message) {
        // Add the message to the queue
        messagesQueue.add(message);
        // Process the message
        System.out.println("Received message: " + message);
        // Handle the message, e.g., update a database or trigger other actions
    }

    @KafkaListener(topics = "statusRec", groupId = "com.example")
    public void listen2(String message) {
        // Add the message to the statusRec queue
        statusRecQueue.add(message);
        // Process the message
        System.out.println("Received message: " + message);
        // Handle the message, e.g., update a database or trigger other actions
    }

    @KafkaListener(topics = "statusRem", groupId = "com.example")
    public void listen3(String message) {
        // Add the message to the statusRec queue
        statusRemQueue.add(message);
        // Process the message
        System.out.println("Received message: " + message);
        // Handle the message, e.g., update a database or trigger other actions
    }


    @KafkaListener(topics = "statusRemm", groupId = "com.example")
    public void listen4(String message) {
        // Add the message to the statusRec queue
        statusRemmQueue.add(message);
        // Process the message
        System.out.println("Received message: " + message);
        // Handle the message, e.g., update a database or trigger other actions
    }

    public ConcurrentLinkedQueue<String> getMessagesQueue() {
        return messagesQueue;
    }

    public void removeMessage(String message) {
        Iterator<String> iterator = messagesQueue.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(message)) {
                iterator.remove();
                break;
            }
        }
    }


    public void clearMessages() {
        messagesQueue.clear();
    }

    public ConcurrentLinkedQueue<String> getStatusRecQueue() {
        return statusRecQueue;
    }

    public void removeMessageFromStatusRecQueue(String message) {
        Iterator<String> iterator = statusRecQueue.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(message)) {
                iterator.remove();
                break;
            }
        }
    }

    public void clearStatusRecQueue() {
        statusRecQueue.clear();
    }








    public ConcurrentLinkedQueue<String> getStatusRemQueue() {
        return statusRemQueue;
    }

    public void removeMessageFromStatusRemQueue(String message) {
        Iterator<String> iterator = statusRemQueue.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(message)) {
                iterator.remove();
                break;
            }
        }
    }

    public void clearStatusRemQueue() {
        statusRemQueue.clear();
    }












    public ConcurrentLinkedQueue<String> getStatusRemmQueue() {
        return statusRemmQueue;
    }

    public void removeMessageFromStatusRemmQueue(String message) {
        Iterator<String> iterator = statusRemmQueue.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(message)) {
                iterator.remove();
                break;
            }
        }
    }

    public void clearStatusRemmQueue() {
        statusRemmQueue.clear();
    }
}
