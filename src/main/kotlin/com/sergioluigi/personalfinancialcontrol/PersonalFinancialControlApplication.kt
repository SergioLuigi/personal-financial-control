package com.sergioluigi.personalfinancialcontrol

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
class PersonalFinancialControlApplication

fun main(args: Array<String>) {
	runApplication<PersonalFinancialControlApplication>(*args)
}






