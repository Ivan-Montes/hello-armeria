package dev.ime.config;

public class GlobalConstants {

	private GlobalConstants() {
		super();
	}
	
	// Patterns
	public static final String PATTERN_STRING_FULL = "^[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ][a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ\\s\\-\\.&,:]{1,49}$";

	// Exceptions
	public static final String EX_SERVERWEBINPUT = "ServerWebInputException";
	public static final String EX_SERVERWEBINPUT_DESC = "Some argument is not supported";
	public static final String EX_PLAIN = "Exception";
	public static final String EX_PLAIN_DESC = "Exception because the night is dark and full of terrors";
	public static final String EX_WEBEXCHANGEBIND = "WebExchangeBindException";
	public static final String EX_WEBEXCHANGEBIND_DESC = "Constraint Violation Exception";	
	
}
