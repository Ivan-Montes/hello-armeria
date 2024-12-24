package dev.ime.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;
import com.linecorp.armeria.client.eureka.EurekaEndpointGroup;
import com.linecorp.armeria.client.Endpoint;
import dev.ime.config.ApplicationProperties;
import dev.ime.config.GlobalConstants;
import dev.ime.exception.GrpcInstancesNotFoundException;


@Component
public class InstancesConnector {

	private final ApplicationProperties applicationProperties;
	private Logger logger = Logger.getLogger(getClass().getName());

	public InstancesConnector(ApplicationProperties applicationProperties) {
		super();
		this.applicationProperties = applicationProperties;
	}
	
	public Map<String, String> getInstanceConnectionInfo(String springApplicationName){	

		Map<String, String> resultMap = new HashMap<>();
	    
		try {			

			EurekaEndpointGroup eurekaEndpointGroup = EurekaEndpointGroup
					.builder(applicationProperties.getEurekaUrl())
                    .appName(springApplicationName)
                    .build();
			CompletableFuture<List<Endpoint>> endpointsFuture = eurekaEndpointGroup.whenReady();
		    List<Endpoint> endpointList = endpointsFuture.get();
			
			if ( !endpointList.isEmpty() ) {

				Endpoint endpoint = endpointList.getFirst();
				
			    String ipAddr = endpoint.ipAddr();
			    String mainPort = String.valueOf(endpoint.port());
			    String customValue = "";
			    
			    resultMap.put(GlobalConstants.SPRAPPNAME, springApplicationName);
				resultMap.put(GlobalConstants.HOST, ipAddr);
				resultMap.put(GlobalConstants.MAIN_PORT, mainPort);	
				resultMap.put("customValue", customValue);					
			
			}

			validateConnectionInfo(resultMap);
			
			return resultMap;
			
		} catch (InterruptedException | ExecutionException ex) {
			Thread.currentThread().interrupt();
	        throw new GrpcInstancesNotFoundException(Map.of(springApplicationName, ex.getMessage()));
	        
	    }catch(Exception ex) {
			
	        throw new GrpcInstancesNotFoundException(Map.of(springApplicationName, ex.getMessage()));	        

		}		
	}	
	
	private void validateConnectionInfo(Map<String, String> resultMap) {
		
		logger.log(Level.INFO, "### Validating connection info: {0}", resultMap);

		String host = resultMap.get(GlobalConstants.HOST);
		String mainPort = resultMap.get(GlobalConstants.MAIN_PORT);
		String springApplicationName = resultMap.get(GlobalConstants.SPRAPPNAME);
		
        if (host == null || host.isEmpty()) {
            throw new IllegalStateException(GlobalConstants.MSG_BAD_HOST + springApplicationName);
        }

        if (mainPort == null || mainPort.isEmpty()) {
            throw new IllegalStateException(GlobalConstants.MSG_BAD_GRPCPORT + springApplicationName);
        }
	}
	
}
