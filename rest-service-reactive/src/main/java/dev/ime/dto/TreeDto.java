package dev.ime.dto;

import dev.ime.config.GlobalConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TreeDto(
		Long treeId,
		@NotBlank @Pattern( regexp = GlobalConstants.PATTERN_STRING_FULL ) String kingdom,
		@NotBlank @Pattern( regexp = GlobalConstants.PATTERN_STRING_FULL ) String family,
		@NotBlank @Pattern( regexp = GlobalConstants.PATTERN_STRING_FULL ) String species
		) {

}
