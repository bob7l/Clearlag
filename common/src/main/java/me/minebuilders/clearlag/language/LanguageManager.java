package me.minebuilders.clearlag.language;

import me.minebuilders.clearlag.ClearLag;
import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.language.messages.Message;
import me.minebuilders.clearlag.modules.BroadcastHandler;
import me.minebuilders.clearlag.modules.ClearlagModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * @author bob7l
 */
public class LanguageManager extends ClearlagModule {

    private static final String[] BUNDLED_LANGUAGES = {
            "English.lang",
            "BrazilianPortuguese.lang",
            "ChineseSimplified.lang",
            "ChineseTraditional.lang",
            "Czech.lang",
            "French.lang",
            "German.lang",
            "Japanese.lang",
            "Korean.lang",
            "Polish.lang",
            "Russian.lang",
            "Spanish.lang"
    };

    private static final String FALLBACK_LANGUAGE = "English.lang";

    @AutoWire
    private BroadcastHandler broadcastHandler;

    @AutoWire
    private ConfigHandler config;

    private LanguageLoader languageLoader;

    private File languagesFolder;

    public Message getMessage(String key) {
        return languageLoader.getMessageByKey(key);
    }

    public LanguageLoader getLanguageLoader() {
        return languageLoader;
    }

    @Override
    public void setEnabled() {
        super.setEnabled();

        languagesFolder = new File(ClearLag.getInstance().getDataFolder(), "languages");
        if (!languagesFolder.exists() && !languagesFolder.mkdirs()) {
            Util.warning("Clearlag failed to create the languages folder!");
        }

        extractBundledLanguages();

        languageLoader = new LanguageLoader(broadcastHandler);

        loadLanguageFromDisk();

        for (Object object : ClearLag.getInstance().getAutoWirer().getWires()) {
            try {
                languageLoader.wireInMessages(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void reload() {
        loadLanguageFromDisk();

        for (Object object : ClearLag.getInstance().getAutoWirer().getWires()) {
            try {
                languageLoader.wireInMessages(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadLanguageFromDisk() {

        String desiredLanguage = config.getConfig().getString("settings.language") + ".lang";
        desiredLanguage = desiredLanguage.substring(0, 1).toUpperCase() + desiredLanguage.substring(1);

        try (InputStream fallbackStream = openLanguageFile(FALLBACK_LANGUAGE)) {
            if (fallbackStream != null) {
                languageLoader.setFallbackLanguageMap(fallbackStream);
            }
        } catch (Exception e) {
            Util.warning("Clearlag failed to load the fallback English language file!");
            e.printStackTrace();
        }

        try (InputStream stream = openLanguageFile(desiredLanguage)) {
            if (stream != null) {
                languageLoader.setLanguageMap(stream);
                Util.log("Loaded language file: " + desiredLanguage);
                return;
            }
            Util.warning("Clearlag FAILED to find your desired language file '" + desiredLanguage + "'. Defaulting to English...");
        } catch (Exception e) {
            Util.warning("Clearlag FAILED to load your desired language file '" + desiredLanguage + "'. Defaulting to English...");
            e.printStackTrace();
        }

        try (InputStream englishStream = openLanguageFile(FALLBACK_LANGUAGE)) {
            if (englishStream != null) {
                languageLoader.setLanguageMap(englishStream);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private InputStream openLanguageFile(String fileName) throws IOException {
        File diskFile = new File(languagesFolder, fileName);
        if (diskFile.isFile()) {
            return new FileInputStream(diskFile);
        }

        java.net.URL bundled = ClearLag.class.getResource("/languages/" + fileName);
        if (bundled != null) {
            return bundled.openStream();
        }

        return null;
    }

    private void extractBundledLanguages() {
        for (String fileName : BUNDLED_LANGUAGES) {
            File target = new File(languagesFolder, fileName);
            if (target.exists()) {
                continue;
            }

            try (InputStream in = ClearLag.class.getResourceAsStream("/languages/" + fileName)) {
                if (in == null) {
                    continue;
                }
                Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Util.log("Extracted language file: " + fileName);
            } catch (IOException e) {
                Util.warning("Failed to extract language file: " + fileName);
                e.printStackTrace();
            }
        }
    }
}