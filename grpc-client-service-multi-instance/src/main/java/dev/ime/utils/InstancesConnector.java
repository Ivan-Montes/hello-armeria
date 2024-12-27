package dev.ime.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import dev.ime.config.GlobalConstants;
import dev.ime.exception.GrpcInstancesNotFoundException;

@Component
public class InstancesConnector {
	
	private final EurekaClient eurekaClient;
	private Logger logger = Logger.getLogger(getClass().getName());

	public InstancesConnector(EurekaClient eurekaClient) {
		super();
		this.eurekaClient = eurekaClient;
	}

	public Map<String, String> getInstanceConnectionInfo(String springApplicationName){	
			
		Map<String, String> resultMap = new HashMap<>();
		
		try {
			
			InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(springApplicationName, false);
		    String host = instanceInfo.getIPAddr();
		    String grpcPort = instanceInfo.getMetadata().get(GlobalConstants.GRPC_PORT);
			resultMap.put(GlobalConstants.SPRAPPNAME, springApplicationName);
			resultMap.put(GlobalConstants.HOST, host);
			resultMap.put(GlobalConstants.GRPC_PORT, grpcPort);
			
			validateConnectionInfo(resultMap);

			return resultMap;
			
		}catch(Exception ex) {
			
	        throw new GrpcInstancesNotFoundException(Map.of(springApplicationName, ex.getMessage()));	        

		}
		
	}	
	
	private void validateConnectionInfo(Map<String, String> resultMap) {

		logger.log(Level.INFO, "### Validating connection info: {0}", resultMap);

		String host = resultMap.get(GlobalConstants.HOST);
		String grpcPort = resultMap.get(GlobalConstants.GRPC_PORT);
		String springApplicationName = resultMap.get(GlobalConstants.SPRAPPNAME);
		
        if (host == null || host.isEmpty()) {
            throw new IllegalStateException(GlobalConstants.MSG_BAD_HOST + springApplicationName);
        }

        if (grpcPort == null || grpcPort.isEmpty()) {
            throw new IllegalStateException(GlobalConstants.MSG_BAD_GRPCPORT + springApplicationName);
        }
	}
	
}