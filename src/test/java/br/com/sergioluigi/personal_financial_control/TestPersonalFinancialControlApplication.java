package br.com.sergioluigi.personal_financial_control;

import org.springframework.boot.SpringApplication;

public class TestPersonalFinancialControlApplication {

	public static void main(String[] args) {
		SpringApplication.from(PersonalFinancialControlApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
