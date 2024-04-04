package me.minebuilders.clearlag.reflection;

import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.modules.Module;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bob7l
 */
public class AutoWirer {

    private final List<Object> wireables = new ArrayList<>();

    public void addWireable(Object obj) {

        if (obj == null)
            throw new NullPointerException();

        wireables.add(obj);
    }

    public void addWireables(Object[] obs) {

        for (Object obj : obs)
            addWireable(obj);
    }

    public void wireObject(Object object) throws IllegalAccessException {

        Class<?> clazz = object.getClass();

        while (clazz != null && clazz != Object.class && clazz != Module.class) {

            for (Field field : clazz.getDeclaredFields()) {

                if (field.isAnnotationPresent(AutoWire.class)) {

                    field.setAccessible(true);

                    if (field.get(object) == null) {

                        for (Object wire : wireables) {

                            if (field.getType().isAssignableFrom(wire.getClass())) {

                                field.set(object, wire);

                                break;
                            }
                        }
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    public List<Object> getWires() {
        return wireables;
    }
}