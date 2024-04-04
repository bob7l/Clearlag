package me.minebuilders.clearlag.annotations;

import me.minebuilders.clearlag.config.ConfigValueType;

import java.lang.annotation.*;

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
