package de.objektkontor.config.common.smtp;

import de.objektkontor.config.ObservableConfig;
import de.objektkontor.config.annotation.ConfigParameter;

public class HttpProxyConfig extends ObservableConfig {

	@ConfigParameter(description = ""
			+ "Specifies the host name of an HTTP web proxy server that will be used for "
			+ "connections to the mail server.") private String host;
	@ConfigParameter(description = ""
			+ "Specifies the port number for the HTTP web proxy server. "
			+ "Defaults to port 80.") private Integer port;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
}