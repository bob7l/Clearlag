package me.minebuilders.clearlag.annotations;

import java.lang.annotation.*;

/**
 * @author bob7l
 *
 * Auto-wires module dependencies
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface AutoWire {

}
