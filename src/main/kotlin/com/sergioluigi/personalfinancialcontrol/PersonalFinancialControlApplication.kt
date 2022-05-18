package com.sergioluigi.personalfinancialcontrol

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaAuditing
@EnableJpaRepositories
@SpringBootApplication
class PersonalFinancialControlApplication

fun main(args: Array<String>) {
	runApplication<PersonalFinancialControlApplication>(*args)
}






