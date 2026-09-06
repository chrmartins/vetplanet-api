package com.vetplanet;

import org.springframework.boot.SpringApplication;

public class TestVetPlanetApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(VetPlanetApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
