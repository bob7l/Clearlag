package me.minebuilders.clearlag.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author bob7l
 */
public class ReflectionUtil {

    public static Method getMethodByName(Class<?> clazz, String methodName) {
        for (Method meth : clazz.getDeclaredMethods()) {
            if (meth.getName().equals(methodName)) {
                return meth;
            }
        }
        return null;
    }

    public static boolean isClass(String className) {
        try  {
            Class.forName(className);
            return true;
        }  catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isField(Class owner, String fieldName) {
        try  {
            Field field = owner.getDeclaredField(fieldName);
            return field != null;
        }  catch (Exception e) {
            return false;
        }
    }

    public static Object castPrimitedValues(Class<?> field, Object value) {
        if (value instanceof Number) {
            if (field == int.class) {
                return ((Number) value).intValue();
            }
            if (field == long.class) {
                return ((Number) value).longValue();
            }
            if (field == float.class) {
                return ((Number) value).floatValue();
            }
            if (field == double.class) {
                return ((Number) value).doubleValue();
            }
        }
        return value;
    }

    public static Field getField(Class<?> clazz, String name)  {

        try {
            final Field field = clazz.getDeclaredField(name);

            field.setAccessible(true);

            return field;

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static MethodHandle generateGetterMethodHandle(Class<?> clazz, String name)  {

        try {
            final Field field = clazz.getDeclaredField(name);

            field.setAccessible(true);

            return MethodHandles.lookup().unreflectGetter(field);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static MethodHandle generateSetterMethodHandle(Class<?> clazz, String name)  {

        try {
            final Field field = clazz.getDeclaredField(name);

            field.setAccessible(true);

            return MethodHandles.lookup().unreflectSetter(field);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static Class<?> getClass(String package_, String clazz) {

        try {
            return Class.forName(package_ + "." + clazz);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
