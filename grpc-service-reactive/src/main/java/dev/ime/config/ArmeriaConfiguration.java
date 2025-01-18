package dev.ime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;

import dev.ime.error.GrpcExceptionHandler;
import dev.ime.service.ReactiveTreeServiceImpl;
import io.grpc.protobuf.services.ProtoReflectionService;

@Configuration
public class ArmeriaConfiguration {

	@Bean
	ArmeriaServerConfigurator armeriaServerConfigurator(ReactiveTreeServiceImpl reactiveTreeServiceImpl, GrpcExceptionHandler grpcExceptionHandler) {
	   
		final GrpcService grpcService =
                GrpcService.builder()
                           .addService(reactiveTreeServiceImpl)
                           .addService(ProtoReflectionService.newInstance())
                           .enableUnframedRequests(true)                        
                           .exceptionHandler(grpcExceptionHandler)
                           .useBlockingTaskExecutor(true)
                           .build();
		
		return serverBuilder ->  serverBuilder.service(grpcService,
		           LoggingService.newDecorator())
				;
	}
	
}
