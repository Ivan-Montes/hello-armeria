package dev.ime.exception;

import java.util.Map;
import java.util.UUID;

import dev.ime.config.GlobalConstants;

public class GrpcInstancesNotFoundException extends BasicException{

	private static final long serialVersionUID = -7996125466096992444L;

	public GrpcInstancesNotFoundException(Map<String, String> errors) {		
		super(
				UUID.randomUUID(),
				GlobalConstants.EX_GRPCINSTANCESNOTFOUND,
				GlobalConstants.EX_GRPCINSTANCESNOTFOUND_DESC,
				errors
				);
	}	

}
