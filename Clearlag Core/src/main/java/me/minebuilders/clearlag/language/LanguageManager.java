package me.minebuilders.clearlag.language;

import me.minebuilders.clearlag.Clearlag;
import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.language.messages.Message;
import me.minebuilders.clearlag.modules.BroadcastHandler;
import me.minebuilders.clearlag.modules.ClearlagModule;

/**
 * @author bob7l
 */
public class LanguageManager extends ClearlagModule {

    @AutoWire
    private BroadcastHandler broadcastHandler;

    @AutoWire
    private ConfigHandler config;

    private LanguageLoader languageLoader;

    public Message getMessage(String key) {
        return languageLoader.getMessageByKey(key);
    }

    public LanguageLoader getLanguageLoader() {
        return languageLoader;
    }

    @Override
    public void setEnabled() {
        super.setEnabled();

        String desiredLanguage = config.getConfig().getString("settings.language") + ".lang";

        desiredLanguage = desiredLanguage.substring(0, 1).toUpperCase() + desiredLanguage.substring(1);

        languageLoader = new LanguageLoader(broadcastHandler);

        try {

            languageLoader.setLanguageMap(Clearlag.class.getResource("/languages/" + desiredLanguage).openStream());

        } catch (Exception e) {

            Util.warning("Clearlag FAILED to find your desired language file '" + desiredLanguage + "'. Defaulting to English...");

            try {
                languageLoader.setLanguageMap(Clearlag.class.getResource("/languages/English.lang").openStream());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        for (Object object : Clearlag.getInstance().getAutoWirer().getWires()) {

            try {
                languageLoader.wireInMessages(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
