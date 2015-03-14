package de.objektkontor.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author eborth
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigParameter {

    public static String FIELD_NAME = "<field.name>";

    public static String NO_DESCRIPTION = "<no.description>";

    String value() default FIELD_NAME;

    String description() default NO_DESCRIPTION;
}
