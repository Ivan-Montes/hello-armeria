package dev.ime.config;

public class GlobalConstants {

	private GlobalConstants() {
		super();
	}
	
	// Patterns
	public static final String PATTERN_STRING_FULL = "^[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ][a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ\\s\\-\\.&,:]{1,49}$";

	// Exceptions
	public static final String EX_ILLEGALARGUMENT = "IllegalArgumentException";
	public static final String EX_ILLEGALARGUMENT_DESC = "Some argument is not supported";
	public static final String EX_PLAIN = "Exception";
	
}
