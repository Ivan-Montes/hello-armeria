package dev.ime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;

import dev.ime.error.GrpcExceptionHandler;
import dev.ime.service.TreeServiceImpl;
import io.grpc.protobuf.services.ProtoReflectionService;

@Configuration
public class ArmeriaConfiguration {

	@Bean
	ArmeriaServerConfigurator armeriaServerConfigurator(TreeServiceImpl treeServiceImpl, GrpcExceptionHandler grpcExceptionHandler) {
	   
		final GrpcService grpcService =
                GrpcService.builder()
                           .addService(treeServiceImpl)
                           .addService(ProtoReflectionService.newInstance())
                           .supportedSerializationFormats(GrpcSerializationFormats.values())
                           .enableHttpJsonTranscoding(true)
                           .enableUnframedRequests(true)                        
                           .exceptionHandler(grpcExceptionHandler)
                           .useBlockingTaskExecutor(true)
                           .build();
		
		return serverBuilder ->  serverBuilder.service(grpcService,
		           LoggingService.newDecorator())
				;
	}
	
}
