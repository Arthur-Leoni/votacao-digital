package com.example.eleicaodigital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication()
@EnableMongoRepositories
public class VotacaoDigitalApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotacaoDigitalApplication.class, args);
	}

}
