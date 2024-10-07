package com.hracces.openhr.Controllers;

import com.hracces.openhr.Services.KafkaConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public class kafkaConsumerController {

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @GetMapping("/messages")
    public List<String> getMessages() {
        return kafkaConsumerService.getMessages();
    }
}
