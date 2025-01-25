package dev.ime.controller;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.internal.shaded.guava.base.Optional;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.testing.junit5.server.ServerExtension;

import dev.ime.dto.TreeDto;
import dev.ime.service.ServiceLayer;

class ControllerLayerTest {

    private static ServiceLayer serviceLayer;
    private final String path = "/api/v1/trees";
    private final ObjectMapper mapper = new ObjectMapper();
    private final Long treeId0 = 3L;
    private final Long treeId1 = 13L;
    private final String kingdom = "arborus";
    private final String family = "ent";
    private final String species0 = "shepherd";
    private final String species1 = "wild";
    private TreeDto treeDto0;
    private TreeDto treeDto1;
    private WebClient client;
    private List<TreeDto> treeDtoList;
    
    @RegisterExtension
    static final ServerExtension server = new ServerExtension() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            serviceLayer = Mockito.mock(ServiceLayer.class);
            sb.annotatedService(new ControllerLayer(serviceLayer));
        }
    };

    @BeforeEach
    private void setUp() {
    	
    	client = WebClient.of(server.httpUri());
        treeDto0 = new TreeDto(treeId0, kingdom, family, species0);
        treeDto1 = new TreeDto(treeId1, kingdom, family, species1);
        treeDtoList = new ArrayList<>();
        
    }
    
    @Test 
    void create_WithRightParam_shouldReturnObjectCreated() throws JsonProcessingException {
	  
    	Mockito.when(serviceLayer.create(Mockito.any(TreeDto.class))).thenReturn(Optional.of(treeDto0));	  
    	
    	final HttpRequest request = createTreePostRequest(treeDto0);
    	final AggregatedHttpResponse res = client.execute(request).aggregate().join();
	  
    	Assertions.assertThat(res.status()).isEqualTo(HttpStatus.OK);
    	Assertions.assertThat(res.contentUtf8()).isEqualTo(mapper.writeValueAsString(treeDto0));
	  
    }
	  
    private HttpRequest createTreePostRequest(TreeDto content) throws JsonProcessingException {    	
    	
    	return HttpRequest.builder().post(path).content(MediaType.JSON_UTF_8, mapper.writeValueAsString(content)).build();
	  
	}
    
    @Test 
    void update_WithRightParam_shouldReturnObjectUpdated() throws JsonProcessingException {
	  
    	Mockito.when(serviceLayer.update(Mockito.anyLong(), Mockito.any(TreeDto.class))).thenReturn(Optional.of(treeDto0));
	  
    	final HttpRequest request = createTreePutRequest(treeDto0);
    	final AggregatedHttpResponse res = client.execute(request).aggregate().join();
	  
    	Assertions.assertThat(res.status()).isEqualTo(HttpStatus.OK);
    	Assertions.assertThat(res.contentUtf8()).isEqualTo(mapper.writeValueAsString(treeDto0));
	  
    } 
    
    private HttpRequest createTreePutRequest(TreeDto content) throws JsonProcessingException {    	
    	
    	return HttpRequest.builder().put(path + "/" + treeId0).content(MediaType.JSON_UTF_8, mapper.writeValueAsString(content)).build();
	  
	}
    
    @Test
    void deleteById_WithIdParam_shouldReturnTrue() {
    	
        Mockito.when(serviceLayer.delete(Mockito.anyLong())).thenReturn(true);
        
        final AggregatedHttpResponse res = client.delete(path + "/" + treeId0).aggregate().join();
        
        Assertions.assertThat(res.status()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res.contentUtf8()).isEqualTo(Boolean.TRUE.toString()); 
        
    }

    @Test
    void getAll_shouldReturnList() throws JsonProcessingException {
    	
    	treeDtoList.add(treeDto0);
    	treeDtoList.add(treeDto1);
        Mockito.when(serviceLayer.getAll()).thenReturn(treeDtoList);
        
        final AggregatedHttpResponse res = client.get(path).aggregate().join();
        
        Assertions.assertThat(res.status()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res.contentUtf8()).isEqualTo(mapper.writeValueAsString(treeDtoList)); 
        
    }    
    
    @Test
    void getById_WithIdParam_shouldReturnObject() throws JsonProcessingException {
    	
        Mockito.when(serviceLayer.getById(Mockito.anyLong())).thenReturn(Optional.of(treeDto0));
        
        final AggregatedHttpResponse res = client.get(path + "/" + treeId0).aggregate().join();
        
        Assertions.assertThat(res.status()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res.contentUtf8()).isEqualTo(mapper.writeValueAsString(treeDto0)); 
        
    }    
    
}
