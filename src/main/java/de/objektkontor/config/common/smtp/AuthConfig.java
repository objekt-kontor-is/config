package de.objektkontor.config.common.smtp;

import de.objektkontor.config.ObservableConfig;
import de.objektkontor.config.annotation.ConfigParameter;

public class AuthConfig extends ObservableConfig {

	@ConfigParameter(description = ""
			+ "If true, attempt to authenticate the user using the AUTH command. "
			+ "Defaults to false.") private Boolean enable;
	@ConfigParameter(description = ""
			+ "If set, lists the authentication mechanisms to consider, "
			+ "and the order in which to consider them. Only mechanisms supported by the server "
			+ "and supported by the current implementation will be used. "
			+ "The default is \"LOGIN PLAIN DIGEST-MD5 NTLM\", which includes all the authentication "
			+ "mechanisms supported by the current implementation except XOAUTH2.") private String mechanisms;
	@ConfigParameter(description = ""
			+ "If true, prevents use of the AUTH LOGIN command. "
			+ "Default is false.") private Boolean loginDisable;
	@ConfigParameter(description = ""
			+ "If true, prevents use of the AUTH PLAIN command. "
			+ "Default is false.") private Boolean plainDisable;
	@ConfigParameter(description = ""
			+ "If true, prevents use of the AUTH DIGEST-MD5 command. "
			+ "Default is false.") private Boolean digestMd5Disable;
	@ConfigParameter(description = ""
			+ "If true, prevents use of the AUTH NTLM command. "
			+ "Default is false.") private Boolean ntlmDisable;
	@ConfigParameter(description = ""
			+ "The NTLM authentication domain.") private String ntlmDomain;
	@ConfigParameter(description = ""
			+ "NTLM protocol-specific flags. "
			+ "See http://curl.haxx.se/rfc/ntlm.html#theNtlmFlags for details.") private Integer ntlmFlags;
	@ConfigParameter(description = ""
			+ "If true, prevents use of the AUTHENTICATE XOAUTH2 command. "
			+ "Because the OAuth 2.0 protocol requires a special access token instead of a password, "
			+ "this mechanism is disabled by default. Enable it by explicitly setting this "
			+ "property to false or by setting the \"mail.smtp.auth.mechanisms\" "
			+ "property to \"XOAUTH2\".") private Boolean xoauth2Disable;

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

	public Boolean getLoginDisable() {
		return loginDisable;
	}

	public void setLoginDisable(Boolean loginDisable) {
		this.loginDisable = loginDisable;
	}

	public Boolean getPlainDisable() {
		return plainDisable;
	}

	public void setPlainDisable(Boolean plainDisable) {
		this.plainDisable = plainDisable;
	}

	public Boolean getDigestMd5Disable() {
		return digestMd5Disable;
	}

	public void setDigestMd5Disable(Boolean digestMd5Disable) {
		this.digestMd5Disable = digestMd5Disable;
	}

	public Boolean getNtlmDisable() {
		return ntlmDisable;
	}

	public void setNtlmDisable(Boolean ntlmDisable) {
		this.ntlmDisable = ntlmDisable;
	}

	public String getNtlmDomain() {
		return ntlmDomain;
	}

	public void setNtlmDomain(String ntlmDomain) {
		this.ntlmDomain = ntlmDomain;
	}

	public Integer getNtlmFlags() {
		return ntlmFlags;
	}

	public void setNtlmFlags(Integer ntlmFlags) {
		this.ntlmFlags = ntlmFlags;
	}

	public Boolean getXoauth2Disable() {
		return xoauth2Disable;
	}

	public void setXoauth2Disable(Boolean xoauth2Disable) {
		this.xoauth2Disable = xoauth2Disable;
	}
}