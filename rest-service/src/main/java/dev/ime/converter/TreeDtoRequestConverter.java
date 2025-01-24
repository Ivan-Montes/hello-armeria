package dev.ime.converter;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.armeria.common.AggregatedHttpRequest;
import com.linecorp.armeria.common.annotation.Nullable;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.annotation.RequestConverterFunction;

import dev.ime.config.GlobalConstants;
import dev.ime.dto.TreeDto;

public class TreeDtoRequestConverter implements RequestConverterFunction {

    private static final ObjectMapper mapper = new ObjectMapper();

	@Override
	public @Nullable Object convertRequest(ServiceRequestContext ctx, AggregatedHttpRequest request,
			Class<?> expectedResultType, @Nullable ParameterizedType expectedParameterizedResultType) throws Exception {
		
		  if (expectedResultType == TreeDto.class) {
			  
	            final JsonNode jsonNode = mapper.readTree(request.contentUtf8());

	            Long treeId = extractIdAllowedNull(jsonNode, "treeId");
	    		String kingdom = extractString(jsonNode, "kingdom");
	    		String family = extractString(jsonNode, "family");
	    		String species = extractString(jsonNode, "species");
	    		
	            return new TreeDto(treeId, kingdom, family, species);
	        }
		  
	        return RequestConverterFunction.fallthrough();
		
	}
	
	private Long extractIdAllowedNull(JsonNode jsonNode, String field) {
		
	    return Optional.ofNullable(jsonNode.get(field))
	    		.map(JsonNode::textValue)
	    		.filter( str -> str.matches("/d"))
	            .map(Long::valueOf)
	            .orElse(null);
	    
	}
    
    private String extractString(JsonNode jsonNode, String field) {
    	
    	String value = Optional.ofNullable(jsonNode.get(field))
    			.map(JsonNode::textValue)
                .orElse("");
 
		 Pattern compiledPattern = Pattern.compile(GlobalConstants.PATTERN_STRING_FULL);
		 Matcher matcher = compiledPattern.matcher(value);
		 if (!matcher.matches()) {
		     throw new IllegalArgumentException("Error with the parameter: " + field);
		 }
		
		 return value;
    }
    
}
