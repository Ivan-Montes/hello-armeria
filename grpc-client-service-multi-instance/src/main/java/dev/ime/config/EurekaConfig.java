package dev.ime.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.discovery.EurekaClient;

@Configuration
public class EurekaConfig {

    private final EurekaInstanceConfigBean eurekaInstanceConfig;
    private final EurekaClient eurekaClient;
    private final ArmeriaPortsProperties armeriaPortsProperties;    
	
	public EurekaConfig(EurekaInstanceConfigBean eurekaInstanceConfig, EurekaClient eurekaClient, ArmeriaPortsProperties armeriaPortsProperties) {
		super();
		this.eurekaInstanceConfig = eurekaInstanceConfig;
		this.eurekaClient = eurekaClient;
		this.armeriaPortsProperties = armeriaPortsProperties;
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> grpcServerReadyListener() {
		
	    return event -> updateEurekaMetadata(armeriaPortsProperties.getPort());
	    
	}

    private void updateEurekaMetadata(int grpcPort) {
    	
        Map<String, String> metadata = new HashMap<>(eurekaInstanceConfig.getMetadataMap());
        metadata.put(GlobalConstants.GRPC_PORT, String.valueOf(grpcPort));
        eurekaInstanceConfig.setMetadataMap(metadata);
        
        forceEurekaRegistration(metadata);
        
    }
    
    private void forceEurekaRegistration(Map<String, String> metadata) {
    	
        eurekaClient.getApplicationInfoManager().registerAppMetadata(metadata);
        
    }
}
