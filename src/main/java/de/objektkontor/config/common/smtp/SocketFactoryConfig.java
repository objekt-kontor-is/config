package de.objektkontor.config.common.smtp;

import de.objektkontor.config.ObservableConfig;
import de.objektkontor.config.annotation.ConfigParameter;

public class SocketFactoryConfig extends ObservableConfig {

	@ConfigParameter(description = ""
			+ "Specifies the name of a class that implements the javax.net.SocketFactory interface. "
			+ "This class will be used to create SMTP sockets.") private String className;
	@ConfigParameter(description = ""
			+ "If set to true, failure to create a socket using the specified socket factory class "
			+ "will cause the socket to be created using the java.net.Socket class. "
			+ "Defaults to true.") private Boolean fallback;
	@ConfigParameter(description = ""
			+ "Specifies the port to connect to when using the specified socket factory. "
			+ "If not set, the default port will be used.") private Integer port;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Boolean getFallback() {
		return fallback;
	}

	public void setFallback(Boolean fallback) {
		this.fallback = fallback;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
}