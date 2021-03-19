package de.objektkontor.config.valueparser;

import java.net.URL;

public class URLValueParser extends AbtrsactArrayParser<URL> {

	@Override
	public URL parseValue(String value, Class<URL> resultType) throws Exception {
		return new URL(value);
	}
}
