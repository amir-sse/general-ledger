package com.leovegasgroup.generalledger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({LiquibaseProperties.class})
public class GeneralLedgerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeneralLedgerApplication.class, args);
	}

}
