package dev.ime.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import dev.ime.config.TreeMapper;
import dev.ime.dto.TreeDto;
import dev.ime.model.Tree;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ServiceLayer {

	private final Map<Long, Tree> treeRepository = new ConcurrentHashMap<>();
	private final TreeMapper treeMapper;

	public ServiceLayer(TreeMapper treeMapper) {
		super();
		this.treeMapper = treeMapper;
	}

	public Mono<String> helloWorld(String name) {
		
		return Mono.just("Hello World, " +  name);
		
	}
	
    public Flux<TreeDto> getAll() {
    	
    	return Flux.fromIterable(treeRepository.values())
    			.map(treeMapper::fromDomainToDto);
        
    }

	public Mono<TreeDto> getById(Long id) {
        
		return Mono.fromSupplier( () -> treeRepository.get(id))
				.map(treeMapper::fromDomainToDto);
		
    }

    public Mono<TreeDto> create(TreeDto treeDto) {
    	
    	return Mono.fromSupplier( () -> {
    		
    		Long key = generateRandomLong();
    		Tree tree = new Tree(
    				key,
    				treeDto.kingdom(),
    				treeDto.family(),
    				treeDto.species()
    				);
    		
    		treeRepository.put(key, tree);
    		
    		return treeRepository.get(key);
    	})
				.map(treeMapper::fromDomainToDto);    	
        
    }

	private long generateRandomLong() {
	    return java.util.concurrent.ThreadLocalRandom.current().nextLong(1, 1000001);
	}

    public Mono<TreeDto> update(Long id, TreeDto treeDto) {    	
     	
    	return Mono.fromSupplier( () -> {
    		
    		Tree treeFound =  treeRepository.get(id);			
    		
    		if ( treeFound == null ) {
    						
    			return null;
    			
    		}
    		
    		treeFound.setKingdom(treeDto.kingdom());
    		treeFound.setFamily(treeDto.family());
    		treeFound.setSpecies(treeDto.species());
    		
    		treeRepository.replace(id, treeFound);
    		
    		return treeFound;
    		
    	})
				.map(treeMapper::fromDomainToDto); 
        
    }

    public Mono<Boolean> delete(Long id) {
    	
       return Mono.fromSupplier( () -> {
    	   treeRepository.remove(id);
    	   return !treeRepository.containsKey(id);
       });
        
    }
    
}
