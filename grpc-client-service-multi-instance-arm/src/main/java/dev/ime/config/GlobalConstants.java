package dev.ime.config;

public class GlobalConstants {

	private GlobalConstants() {
		super();
	}
	
	// Patterns
	public static final String PATTERN_STRING_FULL = "^[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ][a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ\\s\\-\\.&,:]{1,49}$";
	// Messages
	public static final String MSG_INVALID_ID = "Invalid tree ID";
	public static final String MSG_BAD_VALUE = "Error with value";
	public static final String MSG_BAD_HOST = "Host is null or empty for ";
	public static final String MSG_BAD_GRPCPORT = "gRPC port is null or empty for  ";
	// Models
	public static final String TREE_CAT = "Tree";
	public static final String TREE_ID = "treeId";
	// Exceptions
	public static final String EX_RESOURCENOTFOUND = "ResourceNotFoundException";	
	public static final String EX_RESOURCENOTFOUND_DESC = "The resource has not been found";		
	public static final String EX_ILLEGALARGUMENT = "IllegalArgumentException";
	public static final String EX_ILLEGALARGUMENT_DESC = "Some argument is not supported";
	public static final String EX_PLAIN = "Exception";
	public static final String EX_PLAIN_DESC = "Exception because the night is dark and full of terrors";
	public static final String EX_GRPCINSTANCESNOTFOUND = "GrpcInstancesNotFoundException";	
	public static final String EX_GRPCINSTANCESNOTFOUND_DESC = "No matches for the Grpc virtual host";
	// Microservices
	public static final String GRPCSRV_MULTIARM = "grpc-service-multi-instance-arm";
	// Other
	public static final String GRPC_PORT = "grpcPort";
	public static final String HOST = "hostname";
	public static final String SPRAPPNAME = "spring-application-name";
	public static final String MAIN_PORT = "mainPort";
	
}
