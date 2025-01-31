package dev.ime.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import dev.ime.error.ErrorHandler;
import dev.ime.api.validation.DtoValidator;
import dev.ime.dto.TreeDto;
import dev.ime.service.ServiceLayer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest({ControllerLayerHandler.class, ControllerLayerRouter.class})
class ControllerLayerHandlerTest {

 	@MockitoBean
    private ServiceLayer serviceLayer;
 	@MockitoBean
	private DtoValidator dtoValidator;
 	@MockitoBean
	private ErrorHandler errorHandler;

    @Autowired
    private WebTestClient webTestClient;    

    private final String path = "/api/v1/trees";
    private final Long treeId0 = 3L;
    private final Long treeId1 = 13L;
    private final String kingdom = "arborus";
    private final String family = "ent";
    private final String species0 = "shepherd";
    private final String species1 = "wild";
    private TreeDto treeDto0;
    private TreeDto treeDto1;
    
    @BeforeEach
    private void setUp() {
    	
        treeDto0 = new TreeDto(treeId0, kingdom, family, species0);
        treeDto1 = new TreeDto(treeId1, kingdom, family, species1);
        
    }

	@Test
	void getAll_shouldReturnElementsDto() {

        Mockito.when(serviceLayer.getAll()).thenReturn(Flux.just(treeDto0, treeDto1));
		
        webTestClient
		.get().uri(path)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(TreeDto.class)
        .hasSize(2);
        
		Mockito.verify(serviceLayer).getAll();        

	}

	@Test
	void getById_shouldReturnMonoEntity() {
		
		Mockito.when(serviceLayer.getById(Mockito.anyLong())).thenReturn(Mono.just(treeDto0));

		webTestClient
		.get().uri(path + "/{id}", treeId0)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(TreeDto.class)
        .value( dto -> {
			org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(dto).isNotNull(),
					() -> Assertions.assertThat(dto.treeId()).isEqualTo(treeId0)		
					);			
		});
		
		Mockito.verify(serviceLayer).getById(Mockito.anyLong());

	}

	@Test
	void create_WithValidParams_ReturnsCreatedEntityDto() {
		
		Mockito.when(dtoValidator.validateDto(Mockito.any(TreeDto.class))).thenReturn(Mono.just(treeDto0));
		Mockito.when(serviceLayer.create(Mockito.any(TreeDto.class))).thenReturn(Mono.just(treeDto0));
		
		webTestClient
        .post().uri(path)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(treeDto0), TreeDto.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody(TreeDto.class)
        .value(result -> {
        	org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(result).isNotNull(),
					() -> Assertions.assertThat(result.treeId()).isEqualTo(treeDto0.treeId())
					);
        	});	        
		Mockito.verify(dtoValidator).validateDto(Mockito.any(TreeDto.class));
		Mockito.verify(serviceLayer).create(Mockito.any(TreeDto.class));
		
	}

	@Test
	void update_WithValidParams_ReturnsUpdatedEntityDto() {
		
		Mockito.when(dtoValidator.validateDto(Mockito.any(TreeDto.class))).thenReturn(Mono.just(treeDto0));
		Mockito.when(serviceLayer.update(Mockito.anyLong(),Mockito.any(TreeDto.class))).thenReturn(Mono.just(treeDto0));
		
		webTestClient
        .put().uri(path + "/{id}", treeId0)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(treeDto0), TreeDto.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody(TreeDto.class)
        .value(result -> {
        	org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(result).isNotNull(),
					() -> Assertions.assertThat(result.treeId()).isEqualTo(treeDto0.treeId())
					);
        	});	        
		Mockito.verify(dtoValidator).validateDto(Mockito.any(TreeDto.class));
		Mockito.verify(serviceLayer).update(Mockito.anyLong(),Mockito.any(TreeDto.class));
		
	}

	@Test
	void delete_WithValidParams_ReturnsTrue() {
		
		Mockito.when(serviceLayer.delete(Mockito.anyLong())).thenReturn(Mono.just(true));
		
		webTestClient
        .delete().uri(path + "/{id}", treeId0)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Boolean.class)
        .value(result -> {
        	org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(result).isTrue()
					);
        	});	        
		Mockito.verify(serviceLayer).delete(Mockito.anyLong());
		
	}

}
