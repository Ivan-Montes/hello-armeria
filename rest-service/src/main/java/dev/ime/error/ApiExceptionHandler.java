package dev.ime.error;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.annotation.ExceptionHandlerFunction;

import dev.ime.dto.ErrorResponse;
import dev.ime.config.GlobalConstants;


public class ApiExceptionHandler implements ExceptionHandlerFunction {
	
	private final Map<Class<? extends Throwable>, Function<Throwable, HttpResponse>> exceptionHandlers;	
	
	public ApiExceptionHandler() {
		super();
		this.exceptionHandlers = initializeExceptionHandlers();
	}

	private final Map<Class<? extends Throwable>, Function<Throwable, HttpResponse>> initializeExceptionHandlers() {
		
		return Map.of(
				IllegalArgumentException.class, this::handleIllegalArgumentException
				);	
	}

	@Override
	public HttpResponse handleException(ServiceRequestContext ctx, HttpRequest req, Throwable cause) {

        return exceptionHandlers
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().isInstance(cause))
                .findFirst()
                .map(entry -> entry.getValue().apply(cause))
                .orElseGet( ExceptionHandlerFunction::fallthrough );
		
	}

	private HttpResponse handleIllegalArgumentException(Throwable error) {
		
		return HttpResponse.ofJson(
        		new ErrorResponse(
                		UUID.randomUUID(),
                		GlobalConstants.EX_ILLEGALARGUMENT,
                		GlobalConstants.EX_ILLEGALARGUMENT_DESC, 
                		Map.of(GlobalConstants.EX_PLAIN, error.getMessage())
            		));
		
	}
	
}
