package dev.ime.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Empty;
import com.linecorp.armeria.client.grpc.GrpcClients;
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
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
import dev.protobufs.UpdateTreeRequest;
import dev.protobufs.TreeServiceGrpc.TreeServiceBlockingStub;
import dev.protobufs.TreeServiceGrpc.TreeServiceFutureStub;
import dev.protobufs.TreeServiceGrpc.TreeServiceStub;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.stub.StreamObserver;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ReactiveTreeServiceImplTest {

	private static TreeMapper treeMapper;

    private final Long treeId0 = 3L;
    private final Long treeId1 = 13L;
    private final String kingdom = "arborus";
    private final String family = "ent";
    private final String species0 = "shepherd";
    private final String species1 = "wild";
    private TreeServiceBlockingStub treeServiceBlockingStub;
    private TreeServiceStub treeServiceStub;
    private TreeServiceFutureStub treeServiceFutureStub;
    private TreeProto treeProto0;
    private TreeProto treeProto1;
    private List<TreeProto> treeProtoList;

    @RegisterExtension
    static final ServerExtension server = new ServerExtension() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
        	treeMapper = Mockito.mock(TreeMapper.class);
            sb.service(GrpcService.builder()
                                  .addService(new ReactiveTreeServiceImpl(treeMapper))
                                  .addService(ProtoReflectionService.newInstance())
                                  .supportedSerializationFormats(GrpcSerializationFormats.values())
                                  .enableHttpJsonTranscoding(true)
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
    	treeServiceStub = GrpcClients.newClient(
    			server.httpUri(),
    			TreeServiceStub.class);
    	treeServiceFutureStub = GrpcClients.newClient(
    			server.httpUri(),
    			TreeServiceFutureStub.class);
    	treeProto0 = TreeProto.newBuilder().setTreeId(treeId0).setKingdom(kingdom).setFamily(family).setSpecies(species0).build();
    	treeProto1 = TreeProto.newBuilder().setTreeId(treeId1).setKingdom(kingdom).setFamily(family).setSpecies(species1).build();
        treeProtoList = new ArrayList<>();
    	
    }

	@Test
	void createTree_withRightParamAndFutureStub_shouldReturnEntityCreated() {
		
		Mockito.when(treeMapper.fromDomainToProto(Mockito.any(Tree.class))).thenReturn(treeProto0);
		CreateTreeRequest request = CreateTreeRequest.newBuilder()
				.setKingdom(kingdom)
				.setFamily(family)
				.setSpecies(species0)
				.build();		
		
		ListenableFuture<TreeProto> future = treeServiceFutureStub.createTree(request);

		Futures.addCallback(future, new FutureCallback<TreeProto>() {

			@Override
			public void onSuccess(TreeProto result) {
				org.junit.jupiter.api.Assertions.assertAll(
						() -> Assertions.assertThat(result).isNotNull(),
						() -> Assertions.assertThat(result.getSpecies()).isEqualTo(species0)
						);				
			}

			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();				
			}
			
		}, MoreExecutors.directExecutor());
		
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test
	void createTree_withRightParamAndBlockingStub_shouldReturnEntityCreated() {
		
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
	void createTree_withRightParam_shouldReturnEntityCreated() {
	    
	    Mockito.when(treeMapper.fromDomainToProto(Mockito.any(Tree.class))).thenReturn(treeProto0);
	    CreateTreeRequest request = CreateTreeRequest.newBuilder()
	            .setKingdom(kingdom)
	            .setFamily(family)
	            .setSpecies(species0)
	            .build();

	    Mono<TreeProto> result = Mono.create(sink -> {
	        treeServiceStub.createTree(request, new StreamObserver<TreeProto>() {
	            @Override
	            public void onNext(TreeProto value) {
	                sink.success(value);
	            }

	            @Override
	            public void onError(Throwable t) {
	                sink.error(t);
	            }

	            @Override
	            public void onCompleted() {
	                // Complete
	            }
	        });
	    });

	    StepVerifier.create(result)
	        .assertNext(treeProto -> {
	            Assertions.assertThat(treeProto).isNotNull();
	            Assertions.assertThat(treeProto.getSpecies()).isEqualTo(species0);
	        })
	        .verifyComplete();
	}

	@Test
	void updateTree_withUnknownIdAndBlockingStb_shouldReturnException() {
		
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
	void updateTree_withUnknownId_shouldReturnException() {

		Mockito.when(treeMapper.fromDomainToProto(Mockito.any(Tree.class))).thenReturn(treeProto0);
	    UpdateTreeRequest request = UpdateTreeRequest.newBuilder()
	            .setTreeId(treeId0)
	            .setKingdom(kingdom)
	            .setFamily(family)
	            .setSpecies(species0)
	            .build();

	    Mono<TreeProto> result = Mono.create(sink -> {
	        treeServiceStub.updateTree(request, new StreamObserver<TreeProto>() {
	            @Override
	            public void onNext(TreeProto value) {
	                sink.success(value);
	            }

	            @Override
	            public void onError(Throwable t) {
	                sink.error(t);
	            }

	            @Override
	            public void onCompleted() {
	                // Complete
	            }
	        });
	    });

	    StepVerifier.create(result)
	        .expectErrorSatisfies(throwable -> {
	            Assertions.assertThat(throwable)
	                .isInstanceOf(StatusRuntimeException.class)
	                .hasFieldOrPropertyWithValue("status.code", Status.NOT_FOUND.getCode());
	        })
	        .verify();
	}

	@Test
	void deleteTree_withRightParamAndFutureStub_shouldBeTrue() {
		
		DeleteTreeRequest request = DeleteTreeRequest.newBuilder()
				.setTreeId(treeId0)
				.build();

		ListenableFuture<DeleteTreeResponse> future = treeServiceFutureStub.deleteTree(request);

		Futures.addCallback(future, new FutureCallback<DeleteTreeResponse>() {

			@Override
			public void onSuccess(DeleteTreeResponse result) {
				org.junit.jupiter.api.Assertions.assertAll(
						() -> Assertions.assertThat(result.getResult()).isTrue()
						);			
			}

			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();				
			}
			
		}, MoreExecutors.directExecutor());
		
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
	}

	@Test
	void deleteTree_withRightParamAndBlockingStub_shouldThrowsException() {
		
		DeleteTreeRequest request = DeleteTreeRequest.newBuilder()
				.setTreeId(treeId0)
				.build();		
		
		Assertions.assertThatThrownBy(() -> treeServiceBlockingStub.deleteTree(request))
		.isInstanceOf(StatusRuntimeException.class)
		.hasFieldOrPropertyWithValue("status.code", Status.NOT_FOUND.getCode());   
	}

	@Test
	void deleteTree_withRightParam_shouldThrowsException() {
	   
	    DeleteTreeRequest request = DeleteTreeRequest.newBuilder()
	            .setTreeId(treeId0)
	            .build();

	    Mono<DeleteTreeResponse> result = Mono.create(sink -> {
	        treeServiceStub.deleteTree(request, new StreamObserver<DeleteTreeResponse>() {
	            @Override
	            public void onNext(DeleteTreeResponse value) {
	                sink.success(value);
	            }

	            @Override
	            public void onError(Throwable t) {
	                sink.error(t);
	            }

	            @Override
	            public void onCompleted() {
	                // Complete
	            }
	        });
	    });
	    
	    StepVerifier.create(result)
        .expectErrorSatisfies(throwable -> {
            Assertions.assertThat(throwable)
                .isInstanceOf(StatusRuntimeException.class)
                .hasFieldOrPropertyWithValue("status.code", Status.NOT_FOUND.getCode());
        })
        .verify();

	}

	@Test
	void listTrees_WithFutureStub_shouldReturnEntities() {
		
		treeProtoList.add(treeProto0);
		treeProtoList.add(treeProto1);
        Mockito.when(treeMapper.fromDomainListToListProto(Mockito.anyList())).thenReturn(treeProtoList);

		ListenableFuture<ListTreesResponse> future = treeServiceFutureStub.listTrees(Empty.getDefaultInstance());

		Futures.addCallback(future, new FutureCallback<ListTreesResponse>() {

			@Override
			public void onSuccess(ListTreesResponse result) {
		        org.junit.jupiter.api.Assertions.assertAll(
						() -> Assertions.assertThat(result).isNotNull(),
						() -> Assertions.assertThat(result.getTreesCount()).isEqualTo(2)
						); 		
			}

			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();				
			}
			
		}, MoreExecutors.directExecutor());
		
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}       		
        		
	}

	@Test
	void listTrees_WithBlockingStub_shouldReturnEntities() {
		
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
	void listTrees_shouldReturnEntities() {
		
		treeProtoList.add(treeProto0);
		treeProtoList.add(treeProto1);
        Mockito.when(treeMapper.fromDomainListToListProto(Mockito.anyList())).thenReturn(treeProtoList);

        Mono<ListTreesResponse> result = Mono.create(sink -> {
        	treeServiceStub.listTrees(Empty.getDefaultInstance(), new StreamObserver<ListTreesResponse>() {

				@Override
				public void onNext(ListTreesResponse value) {
					sink.success(value);
					
				}

				@Override
				public void onError(Throwable t) {
					sink.error(t);
					
				}

				@Override
				public void onCompleted() {
	                // Complete
					
				}
        		
        	});
        });
        
        StepVerifier.create(result)
        .assertNext( list -> {
        	Assertions.assertThat(list.getTreesCount()).isEqualTo(2);
        })
        .verifyComplete();
        		
	}

	@Test
	void getTree_withUnknownIdAndBlockingStub_shouldReturnException() {		
		
		Mockito.when(treeMapper.fromDomainToProto(Mockito.any(Tree.class))).thenReturn(treeProto0);
		GetTreeRequest request = GetTreeRequest.newBuilder()
				.setTreeId(treeId0)
				.build();	
		
		Assertions.assertThatThrownBy(() -> treeServiceBlockingStub.getTree(request))
		.isInstanceOf(StatusRuntimeException.class)
		.hasFieldOrPropertyWithValue("status.code", Status.NOT_FOUND.getCode());      		
        		
	}

	@Test
	void getTree_withUnknownId_shouldReturnException() {
	    
	    Mockito.when(treeMapper.fromDomainToProto(Mockito.any(Tree.class))).thenReturn(treeProto0);
	    GetTreeRequest request = GetTreeRequest.newBuilder()
	            .setTreeId(treeId0)
	            .build();

	   
	    Mono<TreeProto> result = Mono.create(sink -> {
	        treeServiceStub.getTree(request, new StreamObserver<TreeProto>() {
	            @Override
	            public void onNext(TreeProto value) {
	                sink.success(value);
	            }

	            @Override
	            public void onError(Throwable t) {
	                sink.error(t);
	            }

	            @Override
	            public void onCompleted() {
	                // Complete
	            }
	        });
	    });

	    StepVerifier.create(result)
	        .expectErrorSatisfies(throwable -> {
	            Assertions.assertThat(throwable)
	                .isInstanceOf(StatusRuntimeException.class)
	                .hasFieldOrPropertyWithValue("status.code", Status.NOT_FOUND.getCode());
	        })
	        .verify();
	}	

}
