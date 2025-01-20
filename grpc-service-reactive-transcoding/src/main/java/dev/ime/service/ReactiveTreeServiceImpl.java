package dev.ime.service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
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
    public Mono<ListTreesResponse> listTrees(Mono<com.google.protobuf.Empty> request) {		
				
		return request
				.map( discard -> treeRepository.values())
				.map(ArrayList::new)
				.map(treeMapper::fromDomainListToListProto)
				.map( treeProtoList -> ListTreesResponse.newBuilder()
										.addAllTrees(treeProtoList)
										.build()
										);
				
	}

	@Override
	public Mono<TreeProto> getTree(Mono<GetTreeRequest> request) {        
	
    		return request
    				.map(GetTreeRequest::getTreeId)
    				.map(this::extractId)
    				.filter(treeRepository::containsKey)
    				.map(treeRepository::get)
    				.filter( Objects::nonNull)
    				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.TREE_ID, GlobalConstants.MSG_INVALID_ID))))
    				.map(treeMapper::fromDomainToProto) ;
		
    }
	
	@Override
    public Mono<TreeProto> createTree(Mono<CreateTreeRequest> request) {
		
		return request
				.flatMap(this::createTreeEntity)
				.flatMap(this::addTree)
				.map(treeMapper::fromDomainToProto);
    	        
    }
	
	private Mono<Tree> createTreeEntity(CreateTreeRequest request){
		
		return Mono.fromSupplier( () -> {
			
			Long key = generateRandomLong();
			
			return new Tree(
					key,
					extractString(request.getKingdom()),
					extractString(request.getFamily()),
					extractString(request.getSpecies())
					);	
			
		});		
	}

	private Mono<Tree> addTree(Tree tree){

		return Mono.fromSupplier( () -> {
			
			treeRepository.put(tree.getTreeId(), tree);
			
			return tree;
			
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
    public Mono<TreeProto> updateTree(Mono<UpdateTreeRequest> request) {
    	
		return request
				.map(UpdateTreeRequest::getTreeId)
				.map(this::extractId)
				.filter(treeRepository::containsKey)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.TREE_ID, GlobalConstants.MSG_INVALID_ID))))
				.map(treeRepository::get)
				.flatMap( treeFound -> request.flatMap( updateTreeRequest -> this.updateTreeEntity(updateTreeRequest, treeFound)) )
				.flatMap(this::modTree)
				.map(treeMapper::fromDomainToProto);				
				
    }

	private Mono<Tree> updateTreeEntity(UpdateTreeRequest request, Tree treeFound){
		
		return Mono.fromSupplier( () -> {			

			treeFound.setKingdom(extractString(request.getKingdom()));
			treeFound.setFamily(extractString(request.getFamily()));
			treeFound.setSpecies(extractString(request.getSpecies()));
			
			return treeFound;
			
		});		
	}

	private Mono<Tree> modTree(Tree tree){

		return Mono.fromSupplier( () -> {
			
			treeRepository.replace(tree.getTreeId(), tree);
			
			return tree;
			
		});		
	}

	private Long extractId(Long treeId) {
		
		if (treeId <= 0) {			
	        throw new IllegalArgumentException(GlobalConstants.MSG_INVALID_ID +  " : " + treeId);	        
	    }
		return treeId;
	}

	@Override
    public Mono<DeleteTreeResponse> deleteTree(Mono<DeleteTreeRequest> request) {
    	
		return request
				.map(DeleteTreeRequest::getTreeId)
				.map(this::extractId)
				.filter(treeRepository::containsKey)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.TREE_ID, GlobalConstants.MSG_INVALID_ID))))
				.map(treeRepository::remove)
				.map( tree -> !treeRepository.containsKey(tree.getTreeId()))
				.map( result -> DeleteTreeResponse.newBuilder().setResult(true).build());
		
    }

}
