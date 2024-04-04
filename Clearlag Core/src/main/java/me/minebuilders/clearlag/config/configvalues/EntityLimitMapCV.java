package me.minebuilders.clearlag.config.configvalues;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.entities.AttributeParser;
import me.minebuilders.clearlag.entities.EntityMap;
import org.bukkit.entity.EntityType;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author bob7l
 */
public class EntityLimitMapCV implements ConfigData<EntityMap<Integer>> {

    @AutoWire
    private ConfigHandler configHandler;

    @Override
    public EntityMap<Integer> getValue(String path) {

        final List<String> entities = configHandler.getConfig().getStringList(path);

        final EntityMap<Integer> entityMap = new EntityMap<>();

        final AttributeParser attributeParser = new AttributeParser();

        main:
        for (String entityLine : entities) {

            try {

                final StringTokenizer tokenizer = new StringTokenizer(entityLine);

                final String entityName = tokenizer.nextToken();

                final EntityType entityType = Util.getEntityTypeFromString(entityName);

                if (entityType != null) {

                    try {

                        final AttributeParser.Result result = attributeParser.getAttributesFromString(entityType, tokenizer);

                        for (Iterator<String> iter = result.getNonApplicables().iterator(); iter.hasNext(); ) {

                            final String token = iter.next();

                            if (token.startsWith("limit:")) {

                                iter.remove();

                                try {

                                    for (String nonApplicable : result.getNonApplicables())
                                        Util.warning("Invalid entity attribute path=\"" + path + "\" token=" + nonApplicable + "\"");

                                    final int limit = Integer.parseInt(token.split(":", 2)[1]);

                                    entityMap.set(entityType, result.getAttributes(), limit);

                                } catch (Exception ignored) {
                                    Util.warning("Invalid entity limit path=\"" + path + "\" token=" + token + "\"");
                                }

                                continue main;
                            }
                        }
                    } catch (Exception e) {
                        Util.warning("Failed to read entity path=\"" + path + "\" type=" + entityType.name() + "\" error=\"" + e + "\"");
                    }

                } else {
                    Util.warning("Invalid entity type specified path=\"" + path + "\" value=" + entityName);
                }

            } catch (Exception e) {
                Util.warning("Invalid line specified path=\"" + path + "\" line=" + entityLine);
            }
        }

        return entityMap;
    }
}
