package me.minebuilders.clearlag.config;

import me.minebuilders.clearlag.Clearlag;
import me.minebuilders.clearlag.config.configvalues.*;

/**
 * Created by TCP on 2/3/2016.
 */
public enum ConfigValueType {

    ENTITY_TYPE_TABLE(new EntityTypeTable()),
    WARN_ARRAY(new WarnMapCV()),
    COLORED_STRING(new ColoredStringCV()),
    COLORED_STRINGS(new ColoredStringsCV()),
    MATERIAL_SET(new MaterialSetCV()),
    MATERIAL_INT_MAP(new MaterialIntegerMapCV()),
    ENTITY_LIMIT_MAP(new EntityLimitMapCV()),
    STRING_SET(new StringSetCV()),
    PRIMITIVE(new PrimitiveCV());

    private final ConfigData configData;

    ConfigValueType(ConfigData configData) {
        this.configData = configData;

        try {
            Clearlag.getInstance().getAutoWirer().wireObject(configData);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public ConfigData getConfigData() {
        return configData;
    }

}
