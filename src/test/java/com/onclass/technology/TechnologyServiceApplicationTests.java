package com.onclass.technology;

import com.onclass.technology.domain.api.TechnologyServicePort;
import com.onclass.technology.domain.spi.TechnologyPersistencePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = TechnologyServiceApplication.class)
class TechnologyServiceApplicationTests {

	@MockBean
	private TechnologyPersistencePort technologyPersistencePort;

	@Autowired
	private TechnologyServicePort technologyServicePort;

	@Test
	void contextLoads() {
		// just checks Spring context starts with our beans wired
	}
}
