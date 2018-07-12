package de.objektkontor.config.common.smtp;

import de.objektkontor.config.ObservableConfig;
import de.objektkontor.config.annotation.ConfigParameter;

public class SaslConfig extends ObservableConfig {

	@ConfigParameter(description = ""
			+ "If set to true, attempt to use the javax.security.sasl package to "
			+ "choose an authentication mechanism for login. "
			+ "Defaults to false.") private Boolean enable;
	@ConfigParameter(description = ""
			+ "A space or comma separated list of SASL mechanism names to try to use.") private String mechanisms;
	@ConfigParameter(description = ""
			+ "The authorization ID to use in the SASL authentication. If not set, "
			+ "the authentication ID (user name) is used.") private String authorizationId;
	@ConfigParameter(description = ""
			+ "The realm to use with DIGEST-MD5 authentication.") private String realm;
	@ConfigParameter(description = ""
			+ "If set to true, the canonical host name returned by InetAddress.getCanonicalHostName "
			+ "is passed to the SASL mechanism, instead of the host name used to connect. "
			+ "Defaults to false.") private Boolean useCanonicalHostName;

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getMechanisms() {
		return mechanisms;
	}

	public void setMechanisms(String mechanisms) {
		this.mechanisms = mechanisms;
	}

	public String getAuthorizationId() {
		return authorizationId;
	}

	public void setAuthorizationId(String authorizationId) {
		this.authorizationId = authorizationId;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public Boolean getUseCanonicalHostName() {
		return useCanonicalHostName;
	}

	public void setUseCanonicalHostName(Boolean useCanonicalHostName) {
		this.useCanonicalHostName = useCanonicalHostName;
	}
}