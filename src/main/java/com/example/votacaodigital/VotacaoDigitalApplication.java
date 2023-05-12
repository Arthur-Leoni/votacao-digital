package com.example.votacaodigital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableMongoRepositories
@EnableSwagger2
@EnableFeignClients
@SpringBootApplication
public class VotacaoDigitalApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotacaoDigitalApplication.class, args);
	}

}
