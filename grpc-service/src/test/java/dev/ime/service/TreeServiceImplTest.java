package dev.ime.service;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import com.google.protobuf.Empty;
import com.linecorp.armeria.client.grpc.GrpcClients;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.testing.junit5.server.ServerExtension;

import dev.ime.config.TreeMapper;
import dev.ime.error.GrpcExceptionHandler;
import dev.ime.model.Tree;
import dev.protobufs.CreateTreeRequest;
import dev.protobufs.DeleteTreeRequest;
import dev.protobufs.DeleteTreeResponse;
import dev.protobufs.GetTreeRequest;
import dev.protobufs.ListTreesResponse;
import dev.protobufs.TreeProto;
import dev.protobufs.TreeServiceGrpc.TreeServiceBlockingStub;
import dev.protobufs.UpdateTreeRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.services.ProtoReflectionService;

class TreeServiceImplTest {

	private static TreeMapper treeMapper;

    private final Long treeId0 = 3L;
    private final Long treeId1 = 13L;
    private final String kingdom = "arborus";
    private final String family = "ent";
    private final String species0 = "shepherd";
    private final String species1 = "wild";
    private TreeServiceBlockingStub treeServiceBlockingStub;
    private TreeProto treeProto0;
    private TreeProto treeProto1;
    private List<TreeProto> treeProtoList;
    
    @RegisterExtension
    static final ServerExtension server = new ServerExtension() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
        	treeMapper = Mockito.mock(TreeMapper.class);
            sb.service(GrpcService.builder()
                                  .addService(new TreeServiceImpl(treeMapper))
                                  .addService(ProtoReflectionService.newInstance())
                                  .enableUnframedRequests(true)         
                                  .exceptionHandler(new GrpcExceptionHandler())
                                  .useBlockingTaskExecutor(true)
                                  .build());
        }
    };

    @BeforeEach
    private void setUp() {
    	
    	treeServiceBlockingStub = GrpcClients.newClient(
    			server.httpUri(),
    			TreeServiceBlockingStub.class);
    	treeProto0 = TreeProto.newBuilder().setTreeId(treeId0).setKingdom(kingdom).setFamily(family).setSpecies(species0).build();
    	treeProto1 = TreeProto.newBuilder().setTreeId(treeId1).setKingdom(kingdom).setFamily(family).setSpecies(species1).build();
        treeProtoList = new ArrayList<>();
    	
    }
    	
	@Test
	void createTree_withRightParam_shouldReturnEntityCreated() {
		
		Mockito.when(treeMapper.fromDomainToProto(Mockito.any(Tree.class))).thenReturn(treeProto0);
		CreateTreeRequest request = CreateTreeRequest.newBuilder()
				.setKingdom(kingdom)
				.setFamily(family)
				.setSpecies(species0)
				.build();		
		
		TreeProto result = treeServiceBlockingStub.createTree(request);
		
		org.junit.jupiter.api.Assertions.assertAll(
				() -> Assertions.assertThat(result).isNotNull(),
				() -> Assertions.assertThat(result.getSpecies()).isEqualTo(species0)
				);
		
	}
	
	@Test
	void updateTree_withUnknownId_shouldReturnException() {
		
		Mockito.when(treeMapper.fromDomainToProto(Mockito.any(Tree.class))).thenReturn(treeProto0);
		UpdateTreeRequest request = UpdateTreeRequest.newBuilder()
				.setTreeId(treeId0)
				.setKingdom(kingdom)
				.setFamily(family)
				.setSpecies(species0)
				.build();	
		
		Assertions.assertThatThrownBy(() -> treeServiceBlockingStub.updateTree(request))
		.isInstanceOf(StatusRuntimeException.class)
		.hasFieldOrPropertyWithValue("status.code", Status.NOT_FOUND.getCode());

	}
	
	@Test
	void deleteTree_withRightParam_shouldBeTrue() {
		
		DeleteTreeRequest request = DeleteTreeRequest.newBuilder()
				.setTreeId(treeId0)
				.build();		
		
		DeleteTreeResponse result = treeServiceBlockingStub.deleteTree(request);
		
		org.junit.jupiter.api.Assertions.assertAll(
				() -> Assertions.assertThat(result.getResult()).isTrue()
				);
		
	}

	@Test
	void listTrees_shouldReturnEntities() {
		
		treeProtoList.add(treeProto0);
		treeProtoList.add(treeProto1);
        Mockito.when(treeMapper.fromDomainListToListProto(Mockito.anyList())).thenReturn(treeProtoList);

        ListTreesResponse result = treeServiceBlockingStub.listTrees(Empty.getDefaultInstance());
        
        org.junit.jupiter.api.Assertions.assertAll(
				() -> Assertions.assertThat(result).isNotNull(),
				() -> Assertions.assertThat(result.getTreesCount()).isEqualTo(2)
				);        		
        		
	}

	@Test
	void getTree_withUnknownId_shouldReturnException() {		
		
		Mockito.when(treeMapper.fromDomainToProto(Mockito.any(Tree.class))).thenReturn(treeProto0);
		GetTreeRequest request = GetTreeRequest.newBuilder()
				.setTreeId(treeId0)
				.build();	
		
		Assertions.assertThatThrownBy(() -> treeServiceBlockingStub.getTree(request))
		.isInstanceOf(StatusRuntimeException.class)
		.hasFieldOrPropertyWithValue("status.code", Status.NOT_FOUND.getCode());      		
        		
	}
	
}
