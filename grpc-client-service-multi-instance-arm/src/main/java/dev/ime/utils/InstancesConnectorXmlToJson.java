package dev.ime.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Component;

import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;

import dev.ime.config.ApplicationProperties;
import dev.ime.config.GlobalConstants;
import dev.ime.exception.GrpcInstancesNotFoundException;

@Component
public class InstancesConnectorXmlToJson {

	private final ApplicationProperties applicationProperties;
	private Logger logger = Logger.getLogger(getClass().getName());	

	public InstancesConnectorXmlToJson(ApplicationProperties applicationProperties, Logger logger) {
		super();
		this.applicationProperties = applicationProperties;
		this.logger = logger;
	}

	public Map<String, String> getInstanceConnectionInfo(String springApplicationName){	

		Map<String, String> resultMap = new HashMap<>();
	    
		try {	
			
			WebClient webClient = WebClient.of(applicationProperties.getEurekaUrl());		
			AggregatedHttpResponse response = webClient.get("apps/" + springApplicationName)
				    .aggregate().join();
			
			
			if ( response.status().isSuccess() ) {
				
				String xmlContent = response.contentUtf8();
			    JSONObject jsonObject = XML.toJSONObject(xmlContent);
			    JSONObject application = jsonObject.getJSONObject("application");			    
			    Object instanceObj = application.get("instance");
			    JSONObject instance;
			    
			    if (instanceObj instanceof JSONArray instances) {
			    	
			        instance = instances.getJSONObject(0);
			        
			    } else  {
			        instance = (JSONObject) instanceObj;
			    }
			    
			    JSONObject metadata = instance.getJSONObject("metadata");
			    
			    String ipAddr = instance.getString("ipAddr");
			    String mainPort = String.valueOf(instance.getJSONObject("port").getInt("content"));
			    String customValue = metadata.getString("exampleCustomKey");
			    
			    resultMap.put(GlobalConstants.SPRAPPNAME, springApplicationName);
				resultMap.put(GlobalConstants.HOST, ipAddr);
				resultMap.put(GlobalConstants.MAIN_PORT, mainPort);	
				resultMap.put("customValue", customValue);					
			
			}

			validateConnectionInfo(resultMap);
			
			return resultMap;
			
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
