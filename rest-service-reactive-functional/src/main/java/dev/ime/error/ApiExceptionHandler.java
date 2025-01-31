package dev.ime.error;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import dev.ime.config.GlobalConstants;
import dev.ime.dto.ErrorResponse;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(org.springframework.web.reactive.resource.NoResourceFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNoResourceFoundException(NoResourceFoundException ex) {        

        return Mono.just(ResponseEntity
		        		.status(HttpStatus.NOT_FOUND)
		        		.body(createNoResourceFoundExceptionErrorResponse(ex)));
        		

    }

	private ErrorResponse createNoResourceFoundExceptionErrorResponse(NoResourceFoundException ex) {
		
		return new ErrorResponse(
            UUID.randomUUID(),
            GlobalConstants.EX_RESOURCENOTFOUND,
            GlobalConstants.EX_RESOURCENOTFOUND_DESC,
            Map.of(ex.getLocalizedMessage(), ex.getMessage())
        );
		 
	}
	
	@ExceptionHandler(Exception.class) 
	public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex) {
		
		ErrorResponse response = new ErrorResponse( 
				UUID.randomUUID(),
				GlobalConstants.EX_PLAIN, GlobalConstants.EX_PLAIN_DESC,
				Map.of(" ### class ### ", ex.getClass().getCanonicalName(),
						" ### getMessage ### ", ex.getMessage()
	    ));
		
	    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	    		.body(response));
	  
	}	 

}
