package com.hracces.openhr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@EnableDiscoveryClient
public class OpenHrApplication {

    public static void main(String[] args)  {SpringApplication.run(OpenHrApplication.class, args);}
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
