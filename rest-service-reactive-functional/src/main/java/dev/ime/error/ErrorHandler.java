package dev.ime.error;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.dto.ErrorResponse;
import reactor.core.publisher.Mono;
import dev.ime.config.GlobalConstants;


@Component
public class ErrorHandler {
	
	private final Map<Class<? extends Throwable>, Function<Throwable, Mono<ServerResponse>>> exceptionHandlers;	
	
	public ErrorHandler() {
		super();
		this.exceptionHandlers = initializeExceptionHandlers();
	}

	private final Map<Class<? extends Throwable>, Function<Throwable, Mono<ServerResponse>>> initializeExceptionHandlers() {
		
		return Map.of(
				IllegalArgumentException.class, this::handleIllegalArgumentException
				);	
	}

	public Mono<ServerResponse> handleException(Throwable cause) {

        return exceptionHandlers
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().isInstance(cause))
                .findFirst()
                .map(entry -> entry.getValue().apply(cause))
                .orElseGet( () -> handleGenericException(cause) );

	}

    
    private Mono<ServerResponse> handleGenericException(Throwable error) {
    	
        return ServerResponse
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                		new ErrorResponse(
	                		UUID.randomUUID(),
	                		error.getClass().getSimpleName(),
	                		GlobalConstants.EX_PLAIN_DESC, 
	                		Map.of(GlobalConstants.EX_PLAIN, error.getMessage())
                		));
    
    }

	public Mono<ServerResponse> handleIllegalArgumentException(Throwable error) {
		
		return ServerResponse
				.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.APPLICATION_JSON)
				.bodyValue(
		        		new ErrorResponse(
		                		UUID.randomUUID(),
		                		GlobalConstants.EX_ILLEGALARGUMENT,
		                		GlobalConstants.EX_ILLEGALARGUMENT_DESC, 
		                		Map.of(GlobalConstants.EX_PLAIN, error.getMessage())
		            		));		

	}
	
}
