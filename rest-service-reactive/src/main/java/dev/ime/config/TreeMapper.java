package dev.ime.config;

import java.util.List;

import org.springframework.stereotype.Component;

import dev.ime.dto.TreeDto;
import dev.ime.model.Tree;

@Component
public class TreeMapper {

	
	public TreeDto fromDomainToDto(Tree domain) {
		
		return new TreeDto(
				domain.getTreeId(),
				domain.getKingdom(),
				domain.getFamily(),
				domain.getSpecies()
				);
		
	}
	
	public Tree fromDtoToDomain(TreeDto dto) {
		
		return new Tree(
				dto.treeId(),
				dto.kingdom(),
				dto.family(),
				dto.species()
				);		
		
	}
	
	public List<TreeDto> fromDomainListToListDto(List<Tree> list){
		
		return list
				.stream()
				.map(this::fromDomainToDto)
				.toList();
		
	}
	
}
