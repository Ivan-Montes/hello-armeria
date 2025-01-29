package dev.ime.config;

public class GlobalConstants {

	private GlobalConstants() {
		super();
	}
	
	// Patterns
	public static final String PATTERN_STRING_FULL = "^[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ][a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ\\s\\-\\.&,:]{1,49}$";

	// Exceptions
	public static final String EX_RESOURCENOTFOUND = "ResourceNotFoundException";	
	public static final String EX_RESOURCENOTFOUND_DESC = "The resource has not been found";		
	public static final String EX_PLAIN = "Exception";
	public static final String EX_PLAIN_DESC = "Exception because the night is dark and full of terrors";
	public static final String EX_ILLEGALARGUMENT = "IllegalArgumentException";
	public static final String EX_ILLEGALARGUMENT_DESC = "Some argument is not supported";
}
