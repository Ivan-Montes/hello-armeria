package dev.ime.error;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import dev.ime.config.GlobalConstants;
import dev.ime.dto.ErrorResponse;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(org.springframework.web.server.ServerWebInputException.class)
	public Mono<ResponseEntity<ErrorResponse>> handleServerWebInputException(ServerWebInputException ex){
		
		ErrorResponse response = new ErrorResponse(
				UUID.randomUUID(),
				GlobalConstants.EX_SERVERWEBINPUT,
				GlobalConstants.EX_SERVERWEBINPUT_DESC, 
				Map.of(GlobalConstants.EX_PLAIN, ex.getMessage()));

	    return Mono.just(ResponseEntity
	        .status(HttpStatus.BAD_REQUEST)
	        .body(response));
	    
	}
		
	@ExceptionHandler(org.springframework.web.bind.support.WebExchangeBindException.class)		
	public Mono<ResponseEntity<ErrorResponse>> handleWebExchangeBindException(WebExchangeBindException ex){

		ErrorResponse response =  new ErrorResponse( UUID.randomUUID(),
				GlobalConstants.EX_WEBEXCHANGEBIND,
				GlobalConstants.EX_WEBEXCHANGEBIND_DESC,
				Map.of(GlobalConstants.EX_PLAIN, ex.getMessage()));
				
	    return Mono.just(ResponseEntity
		        .status(HttpStatus.BAD_REQUEST)
		        .body(response));
		
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
