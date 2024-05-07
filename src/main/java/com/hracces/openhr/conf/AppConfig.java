package com.hracces.openhr.conf;


import com.hraccess.openhr.HRSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public HRSessionFactory hrSessionFactory() {
        return HRSessionFactory.getFactory();
    }

}
