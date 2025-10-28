package me.minebuilders.clearlag.annotations;

import me.minebuilders.clearlag.config.ConfigValueType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by TCP on 2/3/2016.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigValue {
    String path() default "";

    ConfigValueType valueType() default ConfigValueType.PRIMITIVE;
}
