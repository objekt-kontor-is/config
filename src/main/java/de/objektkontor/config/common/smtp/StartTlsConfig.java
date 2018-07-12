package de.objektkontor.config.common.smtp;

import de.objektkontor.config.ObservableConfig;
import de.objektkontor.config.annotation.ConfigParameter;

public class StartTlsConfig extends ObservableConfig {

	@ConfigParameter(description = ""
			+ "If true, enables the use of the STARTTLS command (if supported by the server) "
			+ "to switch the connection to a TLS-protected connection before issuing any login commands. "
			+ "Note that an appropriate trust store must configured so that the client "
			+ "will trust the server's certificate. "
			+ "Defaults to false.") private Boolean enable;
	@ConfigParameter(description = ""
			+ "If true, requires the use of the STARTTLS command. If the server doesn't support "
			+ "the STARTTLS command, or the command fails, the connect method will fail. "
			+ "Defaults to false.") private Boolean required;

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}
}