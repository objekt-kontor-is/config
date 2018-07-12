package de.objektkontor.config.common.smtp;

import de.objektkontor.config.ObservableConfig;
import de.objektkontor.config.annotation.ConfigParameter;

public class SslConfig extends ObservableConfig {

	@ConfigParameter(description = ""
			+ "If set to true, use SSL to connect and use the SSL port by default. "
			+ "Defaults to false for the \"smtp\" protocol "
			+ "and true for the \"smtps\" protocol.") private Boolean enable;
	@ConfigParameter(description = ""
			+ "If set to true, check the server identity as specified by RFC 2595. "
			+ "These additional checks based on the content of the server's "
			+ "certificate are intended to prevent man-in-the-middle attacks. "
			+ "Defaults to false.") private Boolean checkServerIdentity;
	@ConfigParameter(description = ""
			+ "If set, and a socket factory hasn't been specified, enables use of a MailSSLSocketFactory. "
			+ "If set to \"*\", all hosts are trusted. If set to a whitespace separated list of hosts, "
			+ "those hosts are trusted. Otherwise, trust depends on the "
			+ "certificate the server presents.") private String trust;
	@ConfigParameter(description = ""
			+ "If set, specifies the name of a class that extends the javax.net.ssl.SSLSocketFactory class. "
			+ "This class will be used to create SMTP SSL sockets.") private SocketFactoryConfig socketFactory;
	@ConfigParameter(description = ""
			+ "Specifies the SSL protocols that will be enabled for SSL connections. "
			+ "The property value is a whitespace separated list of tokens acceptable to the "
			+ "javax.net.ssl.SSLSocket.setEnabledProtocols method.") private String protocols;
	@ConfigParameter(description = ""
			+ "Specifies the SSL cipher suites that will be enabled for SSL connections. "
			+ "The property value is a whitespace separated list of tokens acceptable to the "
			+ "javax.net.ssl.SSLSocket.setEnabledCipherSuites method.") private String cipherSuites;

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public Boolean getCheckServerIdentity() {
		return checkServerIdentity;
	}

	public void setCheckServerIdentity(Boolean checkServerIdentity) {
		this.checkServerIdentity = checkServerIdentity;
	}

	public String getTrust() {
		return trust;
	}

	public void setTrust(String trust) {
		this.trust = trust;
	}

	public SocketFactoryConfig getSocketFactory() {
		return socketFactory;
	}

	public void setSocketFactory(SocketFactoryConfig socketFactory) {
		this.socketFactory = socketFactory;
	}

	public String getProtocols() {
		return protocols;
	}

	public void setProtocols(String protocols) {
		this.protocols = protocols;
	}

	public String getCipherSuites() {
		return cipherSuites;
	}

	public void setCipherSuites(String cipherSuites) {
		this.cipherSuites = cipherSuites;
	}
}