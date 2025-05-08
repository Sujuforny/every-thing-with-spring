package com.suju.every_thing_with_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EveryThingWithSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(EveryThingWithSpringApplication.class, args);
	}

}
