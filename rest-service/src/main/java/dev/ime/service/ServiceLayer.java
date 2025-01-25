package dev.ime.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.linecorp.armeria.internal.shaded.guava.base.Optional;

import dev.ime.config.TreeMapper;
import dev.ime.dto.TreeDto;
import dev.ime.model.Tree;

@Service
public class ServiceLayer {
	
	private final Map<Long, Tree> treeRepository = new ConcurrentHashMap<>();
	private final TreeMapper treeMapper;
	
	public ServiceLayer(TreeMapper treeMapper) {
		super();
		this.treeMapper = treeMapper;
	}

	public List<TreeDto> getAll(){
	
		return treeMapper.fromDomainListToListDto( new ArrayList<>(treeRepository.values()) ); 
		
	}
	
	public Optional<TreeDto> getById(Long id) {
		
		Tree treeFound =  treeRepository.get(id);			

		return treeFound != null? Optional.of(treeMapper.fromDomainToDto(treeFound)):
									 Optional.absent();
		
	}
	
	public Optional<TreeDto> create(TreeDto treeDto){
		
		Long key = generateRandomLong();
		Tree tree = new Tree(
				key,
				treeDto.kingdom(),
				treeDto.family(),
				treeDto.species()
				);
		
		treeRepository.put(key, tree);
		
		TreeDto dto = treeMapper.fromDomainToDto(treeRepository.get(key));
		
		return Optional.of(dto);
		
	}
	
	private long generateRandomLong() {
	    return java.util.concurrent.ThreadLocalRandom.current().nextLong(1, 1000001);
	}

	public Optional<TreeDto> update(Long id, TreeDto treeDto){
		
		Tree treeFound =  treeRepository.get(id);			
		
		if ( treeFound == null ) {
						
			return Optional.absent();
			
		}
		
		treeFound.setKingdom(treeDto.kingdom());
		treeFound.setFamily(treeDto.family());
		treeFound.setSpecies(treeDto.species());
		
		treeRepository.replace(id, treeFound);
		
		return Optional.of(treeMapper.fromDomainToDto(treeFound));
		
	}
	
	public Boolean delete(Long id) {
		
		treeRepository.remove(id);
		
		return !treeRepository.containsKey(id);
		
	}
	
}
