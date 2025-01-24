package dev.ime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;

import dev.ime.controller.ControllerLayer;

@Configuration
public class ArmeriaConfiguration {

	@Bean
	ArmeriaServerConfigurator armeriaServerConfigurator(ControllerLayer controllerLayer) {
	   
		return serverBuilder ->  serverBuilder.annotatedService(controllerLayer)
				.decorator(LoggingService.newDecorator());

	}
	
}
