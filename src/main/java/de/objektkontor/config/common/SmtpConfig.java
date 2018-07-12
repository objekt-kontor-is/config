package de.objektkontor.config.common;

import java.util.Properties;

import de.objektkontor.config.ObservableConfig;
import de.objektkontor.config.annotation.ConfigParameter;
import de.objektkontor.config.common.smtp.AuthConfig;
import de.objektkontor.config.common.smtp.HttpProxyConfig;
import de.objektkontor.config.common.smtp.SaslConfig;
import de.objektkontor.config.common.smtp.SocketFactoryConfig;
import de.objektkontor.config.common.smtp.SocksProxyConfig;
import de.objektkontor.config.common.smtp.SslConfig;
import de.objektkontor.config.common.smtp.StartTlsConfig;

public class SmtpConfig extends ObservableConfig {

	@ConfigParameter(description = ""
			+ "Default user name for SMTP.") private String user;
	@ConfigParameter(description = ""
			+ "The SMTP server to connect to.") private String host;
	@ConfigParameter(description = ""
			+ "The SMTP server port to connect to, if the connect() method "
			+ "doesn't explicitly specify one. Defaults to 25.") private Integer port;
	@ConfigParameter(description = ""
			+ "Socket connection timeout value in milliseconds. "
			+ "This timeout is implemented by java.net.Socket. "
			+ "Default is infinite timeout.") private Integer connectionTimeout;
	@ConfigParameter(description = ""
			+ "Socket read timeout value in milliseconds. "
			+ "This timeout is implemented by java.net.Socket. "
			+ "Default is infinite timeout.") private Integer timeout;
	@ConfigParameter(description = ""
			+ "Socket write timeout value in milliseconds. "
			+ "This timeout is implemented by using a java.util.concurrent.ScheduledExecutorService "
			+ "per connection that schedules a thread to close the socket if the timeout expires. "
			+ "Thus, the overhead of using this timeout is one thread per connection. "
			+ "Default is infinite timeout.") private Integer writeTimeout;
	@ConfigParameter(description = ""
			+ "Email address to use for SMTP MAIL command. "
			+ "This sets the envelope return address. "
			+ "Defaults to msg.getFrom() or InternetAddress.getLocalAddress(). "
			+ "NOTE: mail.smtp.user was previously used for this.") private String from;
	@ConfigParameter(description = ""
			+ "Local host name used in the SMTP HELO or EHLO command. "
			+ "Defaults to InetAddress.getLocalHost().getHostName(). "
			+ "Should not normally need to be set if your JDK and your "
			+ "name service are configured properly.") private String localHost;
	@ConfigParameter(description = ""
			+ "Local address (host name) to bind to when creating the SMTP socket. "
			+ "Defaults to the address picked by the Socket class. "
			+ "Should not normally need to be set, but useful with multi-homed hosts "
			+ "where it's important to pick a particular local address to bind to.") private String localAddress;
	@ConfigParameter(description = ""
			+ "Local port number to bind to when creating the SMTP socket. "
			+ "Defaults to the port number picked by the Socket class.") private Integer localPort;
	@ConfigParameter(description = ""
			+ "If false, do not attempt to sign on with the EHLO command. "
			+ "Defaults to true. Normally failure of the EHLO command will fallback to the HELO command; "
			+ "this property exists only for servers that don't fail EHLO properly or don't implement "
			+ "EHLO properly.") private Boolean ehlo;
	@ConfigParameter(description = ""
			+ "User authentication configuration.") private AuthConfig auth;
	@ConfigParameter(description = ""
			+ "The submitter to use in the AUTH tag in the MAIL FROM command. "
			+ "Typically used by a mail relay to pass along information about the original "
			+ "submitter of the message. See also the setSubmitter method of SMTPMessage. "
			+ "Mail clients typically do not use this.") private String submitter;
	@ConfigParameter(description = ""
			+ "The NOTIFY option to the RCPT command. Either NEVER, or some combination of "
			+ "SUCCESS, FAILURE, and DELAY (separated by commas).") private String dsnNotify;
	@ConfigParameter(description = ""
			+ "The RET option to the MAIL command. Either FULL or HDRS.") private String dsnRet;
	@ConfigParameter(description = ""
			+ "If set to true, and the server supports the 8BITMIME extension, "
			+ "text parts of messages that use the \"quoted-printable\" or \"base64\" "
			+ "encodings are converted to use \"8bit\" encoding if they follow the "
			+ "RFC2045 rules for 8bit text.") private Boolean allow8BitMime;
	@ConfigParameter(description = ""
			+ "If set to true, and a message has some valid and some invalid addresses, "
			+ "send the message anyway, reporting the partial failure with a SendFailedException. "
			+ "If set to false (the default), the message is not sent to any of the recipients "
			+ "if there is an invalid recipient address.") private Boolean sendPartial;
	@ConfigParameter(description = ""
			+ "Sasl configuration") private SaslConfig sasl;
	@ConfigParameter(description = ""
			+ "If set to false, the QUIT command is sent and the connection is immediately closed. "
			+ "If set to true (the default), causes the transport to wait "
			+ "for the response to the QUIT command.") private Boolean quitWait;
	@ConfigParameter(description = ""
			+ "If set to true, causes the transport to include an SMTPAddressSucceededException "
			+ "for each address that is successful. Note also that this will cause a SendFailedException "
			+ "to be thrown from the sendMessage method of SMTPTransport even if all addresses were correct "
			+ "and the message was sent successfully.") private Boolean reportsuccess;
	@ConfigParameter(description = ""
			+ "SockerFactory configuration") private SocketFactoryConfig socketFactory;
	@ConfigParameter(description = ""
			+ "Ssl configuration") private SslConfig ssl;
	@ConfigParameter(description = ""
			+ "If set, enables the use of the STARTTLS command (if supported by the server) "
			+ "to switch the connection to a TLS-protected connection before issuing any login commands. "
			+ "Note that an appropriate trust store must configured so that the client will trust "
			+ "the server's certificate. Defaults to disabled.") private StartTlsConfig startTls;
	@ConfigParameter(description = ""
			+ "Http proxy configuration") private HttpProxyConfig httpProxy;
	@ConfigParameter(description = ""
			+ "Socks proxy configuration") private SocksProxyConfig socksProxy;
	@ConfigParameter(description = ""
			+ "Extension string to append to the MAIL command. "
			+ "The extension string can be used to specify standard SMTP service extensions "
			+ "as well as vendor-specific extensions. Typically the application should use "
			+ "the SMTPTransport method supportsExtension to verify that the server supports "
			+ "the desired service extension. See RFC 1869 and other RFCs "
			+ "that define specific extensions.") private String mailExtension;
	@ConfigParameter(description = ""
			+ "If set to true, use the RSET command instead of the NOOP command in the isConnected method. "
			+ "In some cases sendmail will respond slowly after many NOOP commands; "
			+ "use of RSET avoids this sendmail issue. "
			+ "Defaults to false.") private Boolean useRset;
	@ConfigParameter(description = ""
			+ "If set to true (the default), insist on a 250 response code from the NOOP command "
			+ "to indicate success. The NOOP command is used by the isConnected method to determine "
			+ "if the connection is still alive. Some older servers return the wrong response code on success, "
			+ "some servers don't implement the NOOP command at all and so always return a failure code. "
			+ "Set this property to false to handle servers that are broken in this way. "
			+ "Normally, when a server times out a connection, it will send a 421 response code, "
			+ "which the client will see as the response to the next command it issues. "
			+ "Some servers send the wrong failure response code when timing out a connection. "
			+ "Do not set this property to false when dealing with servers "
			+ "that are broken in this way.") private Boolean noopStrict;

	/**
	 * Apply this configuration valuest to specified properties object
	 *
	 * @param properties
	 */
	public void applyTo(Properties properties) {
		applyTo(properties, "mail.smtp.");
	}

	/**
	 * Apply this configuration valuest to specified properties object
	 * to use with sptms protocol
	 *
	 * @param properties
	 */
	public void applyToSmtps(Properties properties) {
		applyTo(properties, "mail.smtps.");
	}

	private void applyTo(Properties properties, String prefix) {
		setValue(properties, prefix, "user", getUser());
		setValue(properties, prefix, "host", getHost());
		setValue(properties, prefix, "port", getPort());
		setValue(properties, prefix, "connectiontimeout", getConnectionTimeout());
		setValue(properties, prefix, "timeout", getTimeout());
		setValue(properties, prefix, "writetimeout", getWriteTimeout());
		setValue(properties, prefix, "from", getFrom());
		setValue(properties, prefix, "localhost", getLocalHost());
		setValue(properties, prefix, "localaddress", getLocalAddress());
		setValue(properties, prefix, "localport", getLocalPort());
		setValue(properties, prefix, "ehlo", getEhlo());
		AuthConfig auth = getAuth();
		if (auth != null) {
			setValue(properties, prefix, "auth.enable", auth.getEnable());
			setValue(properties, prefix, "auth.mechanisms", auth.getMechanisms());
			setValue(properties, prefix, "auth.login.disable", auth.getLoginDisable());
			setValue(properties, prefix, "auth.plain.disable", auth.getPlainDisable());
			setValue(properties, prefix, "auth.digest-md5.disable", auth.getDigestMd5Disable());
			setValue(properties, prefix, "auth.ntlm.disable", auth.getNtlmDisable());
			setValue(properties, prefix, "auth.ntlm.domain", auth.getNtlmDomain());
			setValue(properties, prefix, "auth.ntlm.flags", auth.getNtlmFlags());
			setValue(properties, prefix, "auth.xoauth2.disable", auth.getXoauth2Disable());
		}
		setValue(properties, prefix, "submitter", getSubmitter());
		setValue(properties, prefix, "dsn.notify", getDsnNotify());
		setValue(properties, prefix, "dsn.ret", getDsnRet());
		setValue(properties, prefix, "allow8bitmime", getAllow8BitMime());
		setValue(properties, prefix, "sendpartial", getSendPartial());
		SaslConfig sasl = getSasl();
		if (sasl != null) {
			setValue(properties, prefix, "sasl.enable", sasl.getEnable());
			setValue(properties, prefix, "sasl.mechanisms", sasl.getMechanisms());
			setValue(properties, prefix, "sasl.authorizationid", sasl.getAuthorizationId());
			setValue(properties, prefix, "sasl.realm", sasl.getRealm());
			setValue(properties, prefix, "sasl.usecanonicalhostname", sasl.getUseCanonicalHostName());
		}
		setValue(properties, prefix, "quitwait", getQuitWait());
		setValue(properties, prefix, "reportsuccess", getReportsuccess());
		SocketFactoryConfig socketFactory = getSocketFactory();
		if (socketFactory != null) {
			setValue(properties, prefix, "socketFactory.class", socketFactory.getClassName());
			setValue(properties, prefix, "socketFactory.fallback", socketFactory.getFallback());
			setValue(properties, prefix, "socketFactory.port", socketFactory.getPort());
		}
		SslConfig ssl = getSsl();
		if (ssl != null) {
			setValue(properties, prefix, "ssl.enable", ssl.getEnable());
			setValue(properties, prefix, "ssl.checkserveridentity", ssl.getCheckServerIdentity());
			setValue(properties, prefix, "ssl.trust", ssl.getTrust());
			socketFactory = ssl.getSocketFactory();
			if (socketFactory != null) {
				setValue(properties, prefix, "ssl.socketFactory.class", socketFactory.getClassName());
				setValue(properties, prefix, "ssl.socketFactory.port", socketFactory.getPort());
			}
			setValue(properties, prefix, "ssl.protocols", ssl.getProtocols());
			setValue(properties, prefix, "ssl.ciphersuites", ssl.getCipherSuites());
		}
		StartTlsConfig startTls = getStartTls();
		if (startTls != null) {
			setValue(properties, prefix, "starttls.enable", startTls.getEnable());
			setValue(properties, prefix, "starttls.required", startTls.getRequired());
		}
		HttpProxyConfig httpProxy = getHttpProxy();
		if (httpProxy != null) {
			setValue(properties, prefix, "proxy.host", httpProxy.getHost());
			setValue(properties, prefix, "proxy.port", httpProxy.getPort());
		}
		SocksProxyConfig socksProxy = getSocksProxy();
		if (socksProxy != null) {
			setValue(properties, prefix, "socks.host", socksProxy.getHost());
			setValue(properties, prefix, "socks.port", socksProxy.getPort());
		}
		setValue(properties, prefix, "mailextension", getMailExtension());
		setValue(properties, prefix, "userset", getUseRset());
		setValue(properties, prefix, "noop.strict", getNoopStrict());
	}

	private void setValue(Properties properties, String prefix, String name, Object value) {
		if (value != null) {
			properties.setProperty(prefix + name, String.valueOf(value));
		}
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

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

	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getWriteTimeout() {
		return writeTimeout;
	}

	public void setWriteTimeout(Integer writeTimeout) {
		this.writeTimeout = writeTimeout;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getLocalHost() {
		return localHost;
	}

	public void setLocalHost(String localHost) {
		this.localHost = localHost;
	}

	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	public Integer getLocalPort() {
		return localPort;
	}

	public void setLocalPort(Integer localPort) {
		this.localPort = localPort;
	}

	public Boolean getEhlo() {
		return ehlo;
	}

	public void setEhlo(Boolean ehlo) {
		this.ehlo = ehlo;
	}

	public AuthConfig getAuth() {
		return auth;
	}

	public void setAuth(AuthConfig auth) {
		this.auth = auth;
	}

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public String getDsnNotify() {
		return dsnNotify;
	}

	public void setDsnNotify(String dsnNotify) {
		this.dsnNotify = dsnNotify;
	}

	public String getDsnRet() {
		return dsnRet;
	}

	public void setDsnRet(String dsnRet) {
		this.dsnRet = dsnRet;
	}

	public Boolean getAllow8BitMime() {
		return allow8BitMime;
	}

	public void setAllow8BitMime(Boolean allow8BitMime) {
		this.allow8BitMime = allow8BitMime;
	}

	public Boolean getSendPartial() {
		return sendPartial;
	}

	public void setSendPartial(Boolean sendPartial) {
		this.sendPartial = sendPartial;
	}

	public SaslConfig getSasl() {
		return sasl;
	}

	public void setSasl(SaslConfig sasl) {
		this.sasl = sasl;
	}

	public Boolean getQuitWait() {
		return quitWait;
	}

	public void setQuitWait(Boolean quitWait) {
		this.quitWait = quitWait;
	}

	public Boolean getReportsuccess() {
		return reportsuccess;
	}

	public void setReportsuccess(Boolean reportsuccess) {
		this.reportsuccess = reportsuccess;
	}

	public SocketFactoryConfig getSocketFactory() {
		return socketFactory;
	}

	public void setSocketFactory(SocketFactoryConfig socketFactory) {
		this.socketFactory = socketFactory;
	}

	public SslConfig getSsl() {
		return ssl;
	}

	public void setSsl(SslConfig ssl) {
		this.ssl = ssl;
	}

	public StartTlsConfig getStartTls() {
		return startTls;
	}

	public void setStartTls(StartTlsConfig startTls) {
		this.startTls = startTls;
	}

	public HttpProxyConfig getHttpProxy() {
		return httpProxy;
	}

	public void setHttpProxy(HttpProxyConfig httpProxy) {
		this.httpProxy = httpProxy;
	}

	public SocksProxyConfig getSocksProxy() {
		return socksProxy;
	}

	public void setSocksProxy(SocksProxyConfig socksProxy) {
		this.socksProxy = socksProxy;
	}

	public String getMailExtension() {
		return mailExtension;
	}

	public void setMailExtension(String mailExtension) {
		this.mailExtension = mailExtension;
	}

	public Boolean getUseRset() {
		return useRset;
	}

	public void setUseRset(Boolean useRset) {
		this.useRset = useRset;
	}

	public Boolean getNoopStrict() {
		return noopStrict;
	}

	public void setNoopStrict(Boolean noopStrict) {
		this.noopStrict = noopStrict;
	}
}
