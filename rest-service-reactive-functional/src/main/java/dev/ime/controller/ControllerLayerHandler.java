package dev.ime.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.error.ErrorHandler;
import dev.ime.api.validation.DtoValidator;
import dev.ime.dto.TreeDto;
import dev.ime.service.ServiceLayer;
import reactor.core.publisher.Mono;

@Component
public class ControllerLayerHandler {

	private final ServiceLayer serviceLayer;
	private final ErrorHandler errorHandler;
	private final DtoValidator dtoValidator;

	public ControllerLayerHandler(ServiceLayer serviceLayer, DtoValidator dtoValidator, ErrorHandler errorHandler) {
		super();
		this.serviceLayer = serviceLayer;
		this.errorHandler = errorHandler;
		this.dtoValidator = dtoValidator;
	}
	
	public Mono<ServerResponse> create(ServerRequest serverRequest) {
		
		return serverRequest.bodyToMono(TreeDto.class)
				.flatMap(dtoValidator::validateDto)
				.flatMap(serviceLayer::create)
				.flatMap( objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(ServerResponse.notFound().build())
				.onErrorResume(errorHandler::handleException);		
		
	}
	
	public Mono<ServerResponse> update(ServerRequest serverRequest) {
		
		return Mono.justOrEmpty(serverRequest.pathVariable("id"))
				.map(Long::parseLong)
				.flatMap( id -> serverRequest.bodyToMono(TreeDto.class)				
						.flatMap(dtoValidator::validateDto)
						.flatMap( dto -> serviceLayer.update(id, dto))
						)
				.flatMap( objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(ServerResponse.notFound().build())
				.onErrorResume(errorHandler::handleException);	
		
	}

	public Mono<ServerResponse> delete(ServerRequest serverRequest) {
		
		return Mono.justOrEmpty(serverRequest.pathVariable("id"))
				.map(Long::parseLong)
		.flatMap(serviceLayer::delete)
		.flatMap( obj -> ServerResponse.ok().bodyValue(obj))
		.switchIfEmpty(ServerResponse.notFound().build())
		.onErrorResume(errorHandler::handleException);		
		
	}

	public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
		   
    	return serviceLayer.getAll()
        .collectList()
        .flatMap(dtos -> ServerResponse.ok().bodyValue(dtos))
		.switchIfEmpty(ServerResponse.notFound().build())
		.onErrorResume(errorHandler::handleException);		
	    
	}

	public Mono<ServerResponse> getById(ServerRequest serverRequest) {
		
		return Mono.justOrEmpty(serverRequest.pathVariable("id"))
				.map(Long::parseLong)
				.flatMap(serviceLayer::getById)
				.flatMap( dto -> ServerResponse.ok().bodyValue(dto))
				.switchIfEmpty(ServerResponse.notFound().build())
				.onErrorResume(errorHandler::handleException);									
	
	}
	
}
