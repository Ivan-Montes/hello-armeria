package dev.ime.error;

import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.common.annotation.Nullable;
import com.linecorp.armeria.common.grpc.GrpcExceptionHandlerFunction;

import dev.ime.config.GlobalConstants;
import dev.ime.exception.GrpcInstancesNotFoundException;
import dev.ime.exception.ResourceNotFoundException;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.Status.Code;

@Component
public class GrpcExceptionHandler implements GrpcExceptionHandlerFunction {

	private Logger logger = Logger.getLogger(getClass().getName());
	
	@Nullable
	@Override
	public Status apply(RequestContext ctx, Status status, Throwable cause, Metadata metadata) {
				
		if (cause instanceof IllegalArgumentException ex) {
			
			logError(GlobalConstants.EX_ILLEGALARGUMENT);	            
			return createStatus(Code.INVALID_ARGUMENT, createDescription(GlobalConstants.EX_ILLEGALARGUMENT, ex.getMessage()));
			
		}

		if (cause instanceof ResourceNotFoundException ex) {
			
			logError(GlobalConstants.EX_RESOURCENOTFOUND);
			return createStatus(Code.NOT_FOUND, createDescription(ex.getName(), ex.getErrors().toString()));
			
		}

		if (cause instanceof GrpcInstancesNotFoundException ex) {
			
			logError(GlobalConstants.EX_GRPCINSTANCESNOTFOUND);
			return createStatus(Code.NOT_FOUND, createDescription(ex.getName(), ex.getErrors().toString()));
			
		}
		
		return null;
		
	}

	private Status createStatus(Code code, String description) {
		
		return Status
		.fromCode(code)
		.withDescription(description);
		
	}

	private String createDescription(String param1, String param2) {
	
		return String.format("%s => %s", param1, param2);
		
	}
	
	private void logError(String msg) {
		
		logger.info( ()-> "### "+ getClass().getSimpleName() + " ### " + msg + " ###");

	}

}
