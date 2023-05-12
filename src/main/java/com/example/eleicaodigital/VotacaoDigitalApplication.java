package com.example.eleicaodigital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableMongoRepositories
@EnableSwagger2
public class VotacaoDigitalApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotacaoDigitalApplication.class, args);
	}

}
