package dev.ime.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "properties")
public class ApplicationProperties {

	private String eurekaUrl;
	private String appName;
	private String instanceId;
	
	public String getEurekaUrl() {
		return eurekaUrl;
	}
	public void setEurekaUrl(String eurekaUrl) {
		this.eurekaUrl = eurekaUrl;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
}
