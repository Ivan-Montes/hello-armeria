package dev.ime.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import dev.ime.config.GlobalConstants;
import dev.ime.config.TreeMapper;
import dev.ime.exception.ResourceNotFoundException;
import dev.ime.model.Tree;
import dev.protobufs.CreateTreeRequest;
import dev.protobufs.DeleteTreeRequest;
import dev.protobufs.DeleteTreeResponse;
import dev.protobufs.GetTreeRequest;
import dev.protobufs.ListTreesResponse;
import dev.protobufs.ReactorTreeServiceGrpc;
import dev.protobufs.TreeProto;
import dev.protobufs.UpdateTreeRequest;
import reactor.core.publisher.Mono;

@Service
public class ReactiveTreeServiceImpl extends ReactorTreeServiceGrpc.TreeServiceImplBase {

	private final Map<Long, Tree> treeRepository = new ConcurrentHashMap<>();
	private final TreeMapper treeMapper;	

	public ReactiveTreeServiceImpl(TreeMapper treeMapper) {
		super();
		this.treeMapper = treeMapper;
	}

	@Override
    public Mono<ListTreesResponse> listTrees(com.google.protobuf.Empty request) {		
				
		return Mono.fromSupplier( () -> {
			
			List<TreeProto> treeProtoList = treeMapper.fromDomainListToListProto( new ArrayList<>(treeRepository.values()) ); 

			return ListTreesResponse.newBuilder()
			.addAllTrees(treeProtoList)
			.build();
			
		});	
	}

	@Override
	public Mono<TreeProto> getTree(GetTreeRequest request) {
        
		return Mono.fromSupplier( () -> {
    		
			Long treeId = extractId(request.getTreeId());
			Tree treeFound =  treeRepository.get(treeId);
			if (treeFound == null) {
		        throw new ResourceNotFoundException(Map.of(GlobalConstants.TREE_ID, String.valueOf(treeId)));
		    }
			
			return treeMapper.fromDomainToProto(treeFound);
			
		});		
    }
	
	@Override
    public Mono<TreeProto> createTree(CreateTreeRequest request) {
		
		return Mono.fromSupplier( () -> {

			Long key = generateRandomLong();
			
			Tree tree = new Tree(
					key,
					extractString(request.getKingdom()),
					extractString(request.getFamily()),
					extractString(request.getSpecies())
					);
			
			treeRepository.put(key, tree);
			
			return treeMapper.fromDomainToProto(tree);
			
		});	   	
    	        
    }

    private String extractString(String rawValue) {
    	
    	String value = Optional.ofNullable(rawValue)
                .orElse("");
 
		 Pattern compiledPattern = Pattern.compile(GlobalConstants.PATTERN_STRING_FULL);
		 Matcher matcher = compiledPattern.matcher(value);
		 if (!matcher.matches()) {
		     throw new IllegalArgumentException(GlobalConstants.MSG_BAD_VALUE + " : " + rawValue);
		 }
		
		 return value;
    }
    
	private long generateRandomLong() {
	    return java.util.concurrent.ThreadLocalRandom.current().nextLong(1, 1000001);
	}

	@Override
    public Mono<TreeProto> updateTree(UpdateTreeRequest request) {
    	
		return Mono.fromSupplier( () -> {

			Long treeId = extractId(request.getTreeId());		
			Tree treeFound =  treeRepository.get(treeId);		
			if (treeFound == null) {
		        throw new ResourceNotFoundException(Map.of(GlobalConstants.TREE_ID, String.valueOf(treeId)));
		    }	
			
			treeFound.setKingdom(extractString(request.getKingdom()));
			treeFound.setFamily(extractString(request.getFamily()));
			treeFound.setSpecies(extractString(request.getSpecies()));
			
			treeRepository.replace(treeId, treeFound);
			
			return treeMapper.fromDomainToProto(treeFound);
			
		});	   	
    	  
    }
    
	private Long extractId(Long treeId) {
		
		if (treeId <= 0) {			
	        throw new IllegalArgumentException(GlobalConstants.MSG_INVALID_ID +  " : " + treeId);	        
	    }
		return treeId;
	}

	@Override
    public Mono<DeleteTreeResponse> deleteTree(DeleteTreeRequest request) {
    	
    	return Mono.fromSupplier( () -> {
    		
    		Long treeId = extractId(request.getTreeId());
    		treeRepository.remove(treeId);
    		Boolean result = !treeRepository.containsKey(treeId);
    		
			return DeleteTreeResponse.newBuilder().setResult(result).build();
			
		});	        
    }

}
