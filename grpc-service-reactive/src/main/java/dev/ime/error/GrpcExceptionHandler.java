package dev.ime.error;

import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.common.annotation.Nullable;
import com.linecorp.armeria.common.grpc.GrpcExceptionHandlerFunction;

import dev.ime.config.GlobalConstants;
import dev.ime.exception.ResourceNotFoundException;
import io.grpc.Metadata;
import io.grpc.Status;

@Component
public class GrpcExceptionHandler implements GrpcExceptionHandlerFunction {

	private Logger logger = Logger.getLogger(getClass().getName());
	
	@Nullable
	@Override
	public Status apply(RequestContext ctx, Status status, Throwable cause, Metadata metadata) {
		
		if (cause instanceof IllegalArgumentException ex) {
			
			logger.info("### "+ getClass().getSimpleName() + " ### " + GlobalConstants.EX_ILLEGALARGUMENT + " ###");
	            
			return Status.INVALID_ARGUMENT
	            		.withDescription(GlobalConstants.EX_ILLEGALARGUMENT + " => " +  ex.getMessage());
			
		}

		if (cause instanceof ResourceNotFoundException ex) {
			
			logger.info("### "+ getClass().getSimpleName() + " ### " + GlobalConstants.EX_RESOURCENOTFOUND + " ###");
	            
			return Status.NOT_FOUND
	            		.withDescription(ex.getName() + " => " + ex.getErrors());
			
		}
		
		return null;
		
	}

}
