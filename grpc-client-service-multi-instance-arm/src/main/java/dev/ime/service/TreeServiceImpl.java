package dev.ime.service;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.linecorp.armeria.client.grpc.GrpcClients;

import dev.ime.config.GlobalConstants;
import dev.ime.utils.InstancesConnector;
import dev.protobufs.CreateTreeRequest;
import dev.protobufs.DeleteTreeRequest;
import dev.protobufs.DeleteTreeResponse;
import dev.protobufs.GetTreeRequest;
import dev.protobufs.ListTreesResponse;
import dev.protobufs.TreeProto;
import dev.protobufs.TreeServiceGrpc;
import dev.protobufs.UpdateTreeRequest;
import dev.protobufs.TreeServiceGrpc.TreeServiceBlockingStub;
import io.grpc.stub.StreamObserver;

@Service
public class TreeServiceImpl extends TreeServiceGrpc.TreeServiceImplBase {

	private final InstancesConnector instancesConnector;

	public TreeServiceImpl(InstancesConnector instancesConnector) {
		super();
		this.instancesConnector = instancesConnector;
	}

	@Override
    public void createTree(CreateTreeRequest request, StreamObserver<TreeProto> responseObserver) {
  		
		validateCreationTreeRequest(request);		
		TreeServiceBlockingStub treeServiceBlockingStub = prepareConnection();		
		TreeProto result = treeServiceBlockingStub.createTree(request);
		
        responseObserver.onNext(result);
        responseObserver.onCompleted();
        
	}

	private void validateCreationTreeRequest(CreateTreeRequest request) {
		
		extractString(request.getKingdom());
		extractString(request.getFamily());
		extractString(request.getSpecies());
		
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

	private TreeServiceBlockingStub prepareConnection() {
		
		Map<String, String> connectionInfoMap = instancesConnector.getInstanceConnectionInfo(GlobalConstants.GRPCSRV_MULTIARM);		

		return createBlockingStub(connectionInfoMap);
		
	}

	private TreeServiceBlockingStub createBlockingStub(Map<String, String> connectionInfoMap) {
		
		return GrpcClients.newClient(
    			"http://" + connectionInfoMap.get(GlobalConstants.HOST) + ":" + connectionInfoMap.get(GlobalConstants.MAIN_PORT),
    			TreeServiceBlockingStub.class);
	}

	@Override
    public void updateTree(UpdateTreeRequest request, StreamObserver<TreeProto> responseObserver) {
			
		validateUpdateTreeRequest(request);
		TreeServiceBlockingStub treeServiceBlockingStub = prepareConnection();		
		TreeProto treeFound = treeServiceBlockingStub.updateTree(request);
		
        responseObserver.onNext(treeFound);
        responseObserver.onCompleted();
        
	}

	private void validateUpdateTreeRequest(UpdateTreeRequest request) {
		
		extractId(request.getTreeId());
		extractString(request.getKingdom());
		extractString(request.getFamily());
		extractString(request.getSpecies());
		
	}

	private Long extractId(Long treeId) {
		
		if (treeId <= 0) {			
	        throw new IllegalArgumentException(GlobalConstants.MSG_INVALID_ID +  " : " + treeId);	        
	    }
		return treeId;
	}

	@Override
    public void deleteTree(DeleteTreeRequest request, StreamObserver<DeleteTreeResponse> responseObserver) {
		
		validateDeleteTreeRequest(request);		
		TreeServiceBlockingStub treeServiceBlockingStub = prepareConnection();		
		DeleteTreeResponse response = treeServiceBlockingStub.deleteTree(request);
		
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        
	}

	private void validateDeleteTreeRequest(DeleteTreeRequest request) {
		
		extractId(request.getTreeId());
		
	}

	@Override
    public void listTrees(Empty request, StreamObserver<ListTreesResponse> responseObserver) {		
		
		TreeServiceBlockingStub treeServiceBlockingStub = prepareConnection();
		ListTreesResponse listTreesResponse = treeServiceBlockingStub.listTrees(request);
		
		responseObserver.onNext(listTreesResponse);
		responseObserver.onCompleted();
	     
	}

	@Override
    public void getTree(GetTreeRequest request, StreamObserver<TreeProto> responseObserver) {
		
		validateGetTreeRequest(request);		
		TreeServiceBlockingStub treeServiceBlockingStub = prepareConnection();		
		TreeProto treeFound = treeServiceBlockingStub.getTree(request);
		
        responseObserver.onNext(treeFound);
		responseObserver.onCompleted();
		
	}

	private void validateGetTreeRequest(GetTreeRequest request) {
		
		extractId(request.getTreeId());
		
	}

}
