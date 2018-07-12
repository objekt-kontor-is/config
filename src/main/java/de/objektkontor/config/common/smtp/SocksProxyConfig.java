package de.objektkontor.config.common.smtp;

import de.objektkontor.config.ObservableConfig;
import de.objektkontor.config.annotation.ConfigParameter;

public class SocksProxyConfig extends ObservableConfig {

	@ConfigParameter(description = ""
			+ "Specifies the host name of a SOCKS5 proxy server that will be used for "
			+ "connections to the mail server.") private String host;
	@ConfigParameter(description = ""
			+ "Specifies the port number for the SOCKS5 proxy server. "
			+ "This should only need to be used if the proxy server is not using the "
			+ "standard port number of 1080.") private Integer port;

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