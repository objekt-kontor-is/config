package de.objektkontor.config.valueparser;

import java.time.Duration;

public class DurationValueParser extends AbtrsactArrayParser<Duration> {

	@Override
	public Duration parseValue(String value, Class<Duration> resultType) throws Exception {
		value = value.toUpperCase();
		value = value.replaceAll(" ", "");
		if (! value.startsWith("P")) {
			value = "PT" + value;
		}
		return Duration.parse(value);
	}
}
