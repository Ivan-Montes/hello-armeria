package dev.ime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GrpcServiceMultiInstanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrpcServiceMultiInstanceApplication.class, args);
	}

}
