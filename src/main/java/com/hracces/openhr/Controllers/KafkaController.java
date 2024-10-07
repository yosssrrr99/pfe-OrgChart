package com.hracces.openhr.Controllers;

import com.hracces.openhr.Services.KafkaListenerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Queue;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaListenerService kafkaListenerService;

    public KafkaController(KafkaListenerService kafkaListenerService) {
        this.kafkaListenerService = kafkaListenerService;
    }

    @GetMapping("/messages")
    public Queue<String> getMessages() {
        // Return the queue of received messages from notifications topic
        return kafkaListenerService.getMessagesQueue();
    }

    @DeleteMapping("/messages/{message}")
    public ResponseEntity<Void> clearMessage(@PathVariable String message) {
        kafkaListenerService.removeMessage(message);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/messages")
    public ResponseEntity<Void> clearAllMessages() {
        kafkaListenerService.clearMessages();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/messagesRec")
    public Queue<String> getMessagesRec() {
        // Return the queue of received messages from statusRec topic
        return kafkaListenerService.getStatusRecQueue();
    }

    @DeleteMapping("/messagesRec/{message}")
    public ResponseEntity<Void> clearMessageRec(@PathVariable String message) {
        kafkaListenerService.removeMessageFromStatusRecQueue(message);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/messagesRec")
    public ResponseEntity<Void> clearAllMessagesRec() {
        kafkaListenerService.clearStatusRecQueue();
        return ResponseEntity.noContent().build();
    }





    @GetMapping("/messagesRem")
    public Queue<String> getMessagesRem() {
        // Return the queue of received messages from statusRec topic
        return kafkaListenerService.getStatusRemQueue();
    }

    @DeleteMapping("/messagesRem/{message}")
    public ResponseEntity<Void> clearMessageRem(@PathVariable String message) {
        kafkaListenerService.removeMessageFromStatusRemQueue(message);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/messagesRem")
    public ResponseEntity<Void> clearAllMessagesRem() {
        kafkaListenerService.clearStatusRemQueue();
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/messagesRemm")
    public Queue<String> getMessagesRemm() {
        // Return the queue of received messages from statusRec topic
        return kafkaListenerService.getStatusRemmQueue();
    }

    @DeleteMapping("/messagesRemm/{message}")
    public ResponseEntity<Void> clearMessageRemm(@PathVariable String message) {
        kafkaListenerService.removeMessageFromStatusRemmQueue(message);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/messagesRemm")
    public ResponseEntity<Void> clearAllMessagesRemm() {
        kafkaListenerService.clearStatusRemmQueue();
        return ResponseEntity.noContent().build();
    }
}

