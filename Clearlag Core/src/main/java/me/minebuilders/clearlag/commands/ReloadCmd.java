package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.Clearlag;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.MessageTree;
import me.minebuilders.clearlag.modules.CommandModule;
import me.minebuilders.clearlag.modules.Module;
import org.bukkit.command.CommandSender;

public class ReloadCmd extends CommandModule {

    @AutoWire
    private ConfigHandler configHandler;

    @LanguageValue(key = "command.reload.")
    private MessageTree lang;

    @Override
    protected void run(CommandSender sender, String[] args) {

        lang.sendMessage("begin", sender);

        for (Module mod : Clearlag.getModules()) {

            if (mod.isEnabled()) {
                ConfigPath configPath = mod.getClass().getAnnotation(ConfigPath.class);

                if ((configPath != null)) {
                    mod.setDisabled();
                }
            }
        }

        configHandler.reloadConfig();

        for (Module mod : Clearlag.getModules()) {

            if (!mod.isEnabled()) {
                ConfigPath configPath = mod.getClass().getAnnotation(ConfigPath.class);

                if (configPath == null || (configHandler.getConfig().get(configPath.path() + ".enabled") == null || configHandler.getConfig().getBoolean(configPath.path() + ".enabled"))) {
                        mod.setEnabled();
                }
            }
        }

        try {
            configHandler.setModuleConfigValues();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Todo: Inject language shit.

        lang.sendMessage("successful", sender);
    }
}
