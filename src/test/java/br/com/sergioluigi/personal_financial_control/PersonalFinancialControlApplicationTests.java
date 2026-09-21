package br.com.sergioluigi.personal_financial_control;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PersonalFinancialControlApplicationTests {

	@Test
	void contextLoads() {
	}

}
