package dev.ime.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;

import dev.ime.config.GlobalConstants;
import dev.ime.config.TreeMapper;
import dev.ime.exception.ResourceNotFoundException;
import dev.ime.model.Tree;
import dev.protobufs.CreateTreeRequest;
import dev.protobufs.DeleteTreeRequest;
import dev.protobufs.DeleteTreeResponse;
import dev.protobufs.GetTreeRequest;
import dev.protobufs.ListTreesResponse;
import dev.protobufs.TreeProto;
import dev.protobufs.TreeServiceGrpc;
import dev.protobufs.UpdateTreeRequest;
import io.grpc.stub.StreamObserver;

@Service
public class TreeServiceImpl extends TreeServiceGrpc.TreeServiceImplBase {

	private final Map<Long, Tree> treeRepository = new ConcurrentHashMap<>();
	private final TreeMapper treeMapper;
	
	public TreeServiceImpl(TreeMapper treeMapper) {
		super();
		this.treeMapper = treeMapper;
	}

	@Override
    public void createTree(CreateTreeRequest request, StreamObserver<TreeProto> responseObserver) {
		
		Long key = generateRandomLong();
		
		Tree tree = new Tree(
				key,
				extractString(request.getKingdom()),
				extractString(request.getFamily()),
				extractString(request.getSpecies())
				);
		
		treeRepository.put(key, tree);
		
        responseObserver.onNext(treeMapper.fromDomainToProto(tree));
        responseObserver.onCompleted();
	}

	private long generateRandomLong() {
	    return java.util.concurrent.ThreadLocalRandom.current().nextLong(1, 1000001);
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
    
	@Override
    public void updateTree(UpdateTreeRequest request, StreamObserver<TreeProto> responseObserver) {
		
		Long treeId = extractId(request.getTreeId());		
		Tree treeFound =  treeRepository.get(treeId);		
		if (treeFound == null) {
	        responseObserver.onError(new ResourceNotFoundException(Map.of(GlobalConstants.TREE_ID, String.valueOf(treeId))));
	        return;
	    }	
		
		treeFound.setKingdom(extractString(request.getKingdom()));
		treeFound.setFamily(extractString(request.getFamily()));
		treeFound.setSpecies(extractString(request.getSpecies()));
		
		treeRepository.replace(treeId, treeFound);
		
        responseObserver.onNext(treeMapper.fromDomainToProto(treeFound));
        responseObserver.onCompleted();
	}

	private Long extractId(Long treeId) {
		
		if (treeId <= 0) {			
	        throw new IllegalArgumentException(GlobalConstants.MSG_INVALID_ID +  " : " + treeId);	        
	    }
		return treeId;
	}

	@Override
    public void deleteTree(DeleteTreeRequest request, StreamObserver<DeleteTreeResponse> responseObserver) {
		
		Long treeId = extractId(request.getTreeId());
		treeRepository.remove(treeId);
		Boolean result = !treeRepository.containsKey(treeId);
		
        responseObserver.onNext(DeleteTreeResponse.newBuilder().setResult(result).build());
        responseObserver.onCompleted();
	}

	@Override
    public void listTrees(Empty request, StreamObserver<ListTreesResponse> responseObserver) {
		
		List<TreeProto> treeProtoList = treeMapper.fromDomainListToListProto( new ArrayList<>(treeRepository.values()) ); 
		ListTreesResponse listTreesResponse = ListTreesResponse.newBuilder()
				.addAllTrees(treeProtoList)
				.build();
		
		responseObserver.onNext(listTreesResponse);
		responseObserver.onCompleted();
	     
	}

	@Override
    public void getTree(GetTreeRequest request, StreamObserver<TreeProto> responseObserver) {
	    
		Long treeId = extractId(request.getTreeId());
		Tree treeFound =  treeRepository.get(treeId);
		if (treeFound == null) {
	        responseObserver.onError(new ResourceNotFoundException(Map.of(GlobalConstants.TREE_ID, String.valueOf(treeId))));
	        return;
	    }
		
        responseObserver.onNext(treeMapper.fromDomainToProto(treeFound));
		responseObserver.onCompleted();
		
	}
	
}
