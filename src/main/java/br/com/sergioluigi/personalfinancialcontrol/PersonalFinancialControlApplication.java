package br.com.sergioluigi.personalfinancialcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication
@EnableJpaRepositories
public class PersonalFinancialControlApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersonalFinancialControlApplication.class, args);
    }
}