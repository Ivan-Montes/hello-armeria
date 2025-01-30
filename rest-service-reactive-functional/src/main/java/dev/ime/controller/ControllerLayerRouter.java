package dev.ime.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class ControllerLayerRouter {
	
	@Bean
	RouterFunction<ServerResponse> commandEndpointRoutes(ControllerLayerHandler controllerLayerHandler) {
		
		return RouterFunctions.nest(RequestPredicates.path("/api/v1/trees"),
			   RouterFunctions.nest(RequestPredicates.accept(MediaType.APPLICATION_JSON),
			   RouterFunctions.route(RequestPredicates.POST(""), controllerLayerHandler::create)
   						   .andRoute(RequestPredicates.PUT("/{id}"), controllerLayerHandler::update)
   						   .andRoute(RequestPredicates.DELETE("/{id}"), controllerLayerHandler::delete)   						   
   						.andRoute(RequestPredicates.GET(""), controllerLayerHandler::getAll)
						.andRoute(RequestPredicates.GET("/{id}"), controllerLayerHandler::getById)
					   ));
	}
	
}
