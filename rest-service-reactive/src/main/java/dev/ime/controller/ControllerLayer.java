package dev.ime.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ime.dto.TreeDto;
import dev.ime.service.ServiceLayer;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/trees")
public class ControllerLayer {

	private final ServiceLayer serviceLayer;

	public ControllerLayer(ServiceLayer serviceLayer) {
		super();
		this.serviceLayer = serviceLayer;
	}
	
	@GetMapping
    public Flux<TreeDto> getAll() {
    	
        return serviceLayer.getAll();
        
    }
    
	@GetMapping("/{id}")
	public Mono<ResponseEntity<TreeDto>> getById(@PathVariable Long id) {
		
		return serviceLayer
				.getById(id)
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));	
		
	}

    @PostMapping
    public Mono<ResponseEntity<TreeDto>> create(@Valid @RequestBody TreeDto treeDto) {
    	
        return serviceLayer.create(treeDto)
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));	
        
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<TreeDto>> update(@PathVariable Long id, @Valid @RequestBody TreeDto treeDto) {
        
    	return serviceLayer.update(id, treeDto)
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));	

    }
    
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Boolean>> delete(@PathVariable Long id) {
    	
        return serviceLayer.delete(id)
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));	
        
    }
    
}
