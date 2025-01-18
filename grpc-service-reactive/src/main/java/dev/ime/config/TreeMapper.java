package dev.ime.config;

import java.util.List;

import org.springframework.stereotype.Component;

import dev.ime.model.Tree;
import dev.protobufs.TreeProto;

@Component
public class TreeMapper {
	
	public TreeProto fromDomainToProto(Tree domain) {
		
		return TreeProto.newBuilder()
				.setTreeId(domain.getTreeId())
				.setKingdom(domain.getKingdom())
				.setFamily(domain.getFamily())
				.setSpecies(domain.getSpecies())
				.build();
	}

	public List<TreeProto> fromDomainListToListProto(List<Tree> list){
		
		return list
				.stream()
				.map(this::fromDomainToProto)
				.toList();
		
	}
	
}
