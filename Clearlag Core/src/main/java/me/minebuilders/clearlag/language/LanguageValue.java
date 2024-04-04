package me.minebuilders.clearlag.language;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LanguageValue {

    String key();

}
