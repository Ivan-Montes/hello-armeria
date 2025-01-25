package dev.ime.controller;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.logging.LogLevel;
import com.linecorp.armeria.internal.shaded.guava.base.Optional;
import com.linecorp.armeria.server.annotation.*;
import com.linecorp.armeria.server.annotation.decorator.LoggingDecorator;

import dev.ime.converter.TreeDtoRequestConverter;
import dev.ime.dto.TreeDto;
import dev.ime.error.ApiExceptionHandler;
import dev.ime.service.ServiceLayer;

import org.springframework.stereotype.Component;

@Component
@PathPrefix("/api/v1/trees")
@ExceptionHandler(ApiExceptionHandler.class)
@LoggingDecorator(requestLogLevel = LogLevel.INFO)
public class ControllerLayer {
	
	public final ServiceLayer serviceLayer;
	
	public ControllerLayer(ServiceLayer serviceLayer) {
		super();
		this.serviceLayer = serviceLayer;
	}

	@Get
    public HttpResponse getAll() {
	   
        return HttpResponse.ofJson(serviceLayer.getAll());
        
    }
	
	@Get("/{id}")
	public HttpResponse getById(@Param Long id) {
	   
		Optional<TreeDto> optTreeFound = serviceLayer.getById(id);
		
		return optTreeFound.isPresent()? HttpResponse.ofJson(optTreeFound.get()):
										 HttpResponse.of(HttpStatus.NOT_FOUND);
	
	}
	
	@Post
	@RequestConverter(TreeDtoRequestConverter.class)
	public HttpResponse create(@RequestObject TreeDto dto) {
		
		Optional<TreeDto> optTreeCreated = serviceLayer.create(dto);

		return optTreeCreated.isPresent()? HttpResponse.ofJson(optTreeCreated.get()):
			 HttpResponse.of(HttpStatus.NOT_FOUND);
		
	}

	@Put("/{id}")
	@RequestConverter(TreeDtoRequestConverter.class)
	public HttpResponse update(@Param Long id, @RequestObject TreeDto dto) {
		
		Optional<TreeDto> optTreeUpdated = serviceLayer.update(id, dto);
		
		return optTreeUpdated.isPresent()? HttpResponse.ofJson(optTreeUpdated.get()):
			 HttpResponse.of(HttpStatus.NOT_FOUND);
		
	}
	
	@Delete("/{id}")
	public HttpResponse deleteById(@Param Long id) {
		   
		return HttpResponse.ofJson(serviceLayer.delete(id));
			
	}
	
}
