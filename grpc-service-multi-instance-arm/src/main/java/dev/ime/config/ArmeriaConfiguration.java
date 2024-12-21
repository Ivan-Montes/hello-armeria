package dev.ime.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linecorp.armeria.server.eureka.EurekaUpdatingListener;
import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;

import dev.ime.error.GrpcExceptionHandler;
import dev.ime.service.TreeServiceImpl;
import io.grpc.protobuf.services.ProtoReflectionService;

@Configuration
public class ArmeriaConfiguration {

	@Bean
	ArmeriaServerConfigurator armeriaServerConfigurator(TreeServiceImpl treeServiceImpl, GrpcExceptionHandler grpcExceptionHandler, ApplicationProperties applicationProperties) {
	   
		final GrpcService grpcService =
                GrpcService.builder()
                           .addService(treeServiceImpl)
                           .addService(ProtoReflectionService.newInstance())                          
                           .enableUnframedRequests(true)                        
                           .exceptionHandler(grpcExceptionHandler)
                           .useBlockingTaskExecutor(true)
                           .build();
		
		Map<String, String> customMetadata = new HashMap<>();
		customMetadata.put("exampleAnyPort", String.valueOf(99));
		customMetadata.put("exampleCustomKey", "customValue");
		
		final EurekaUpdatingListener eurekaListener = 
				EurekaUpdatingListener.builder(applicationProperties.getEurekaUrl())
                .instanceId(applicationProperties.getInstanceId())
                .hostname(applicationProperties.getAppName())
                .metadata(customMetadata)
                .build();
		
		return serverBuilder ->  serverBuilder.service(grpcService,
		           LoggingService.newDecorator())
				.serverListener(eurekaListener);
		
	}
	
}
