package me.minebuilders.clearlag.config.configvalues;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.entities.AttributeParser;
import me.minebuilders.clearlag.entities.EntityTable;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.StringTokenizer;

public class EntityTypeTable implements ConfigData<EntityTable> {

    @AutoWire
    private ConfigHandler configHandler;

    @Override
    public EntityTable getValue(String path) {

        final List<String> entities = configHandler.getConfig().getStringList(path);

        final EntityTable table = new EntityTable();

        final AttributeParser attributeParser = new AttributeParser();

        for (String entityLine : entities) {

            try {

                final StringTokenizer tokenizer = new StringTokenizer(entityLine);

                final String entityName = tokenizer.nextToken();

                final EntityType entityType = Util.getEntityTypeFromString(entityName);

                if (entityType != null) {

                    try {

                        final AttributeParser.Result result = attributeParser.getAttributesFromString(entityType, tokenizer);

                        for (String nonApplicable : result.getNonApplicables())
                            Util.warning("Invalid entity attribute path=\"" + path + "\" token=" + nonApplicable + "\"");

                        if (result.getAttributes().isEmpty()) {
                            table.setEntity(entityType);
                        } else {
                            table.setEntityAttributes(entityType, result.getAttributes());
                        }

                    } catch (Exception e) {
                        Util.warning("Failed to read entity path=\"" + path + "\" type=" + entityType.name() + "\" error=\"" + e + "\"");
                        e.printStackTrace();
                    }

                } else {
                    Util.warning("Invalid entity type specified path=\"" + path + "\" value=" + entityName);
                }

            } catch (Exception e) {
                Util.warning("Invalid line specified path=\"" + path + "\" line=" + entityLine);
            }
        }

        return table;
    }
}
