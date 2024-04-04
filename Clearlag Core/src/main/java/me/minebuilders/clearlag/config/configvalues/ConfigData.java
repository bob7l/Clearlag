package me.minebuilders.clearlag.config.configvalues;

/**
 * Created by TCP on 2/3/2016.
 */
public interface ConfigData<T> {

    T getValue(String path);

}
