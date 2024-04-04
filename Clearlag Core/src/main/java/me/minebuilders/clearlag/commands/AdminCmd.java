package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.Clearlag;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.MessageTree;
import me.minebuilders.clearlag.modules.CommandModule;
import me.minebuilders.clearlag.modules.EventModule;
import me.minebuilders.clearlag.modules.Module;
import me.minebuilders.clearlag.modules.TaskModule;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AdminCmd extends CommandModule {

    @AutoWire
    private ConfigHandler configHandler;

    @LanguageValue(key = "command.admin.")
    private MessageTree lang;

    @Override
    protected void run(CommandSender sender, String[] args) {

        if (args.length == 0) {

            lang.sendMessage("help", sender);

        } else {

            final String cmd = args[0];

            if (cmd.equalsIgnoreCase("reload")) {

                if (args.length == 1) {

                    StringBuilder sb = new StringBuilder();

                    for (Module m : Clearlag.getModules()) {
                        if (m.isEnabled() && ConfigHandler.containsReloadableFields(m)) {
                            sb.append(", ").append(m.getClass().getSimpleName());
                        }
                    }

                    lang.sendMessage("enabledModules", sender, (sb.length() > 1 ? sb.substring(2) : "None"));

                } else {

                    Module mod = Clearlag.getModule(args[1]);

                    if (mod != null) {

                        if (!ConfigHandler.containsReloadableFields(mod)) {

                            lang.sendMessage("noReloadableFields", sender, mod.getClass().getSimpleName());

                        } else if (!mod.isEnabled()) {

                            lang.sendMessage("notEnabled", sender, mod.getClass().getSimpleName());

                        } else {

                            try {
                                configHandler.reloadConfig();

                                configHandler.setObjectConfigValues(mod);

                                lang.sendMessage("reload", sender, mod.getClass().getSimpleName());

                            } catch (Exception e) {
                                lang.sendMessage("failedReload", sender, mod.getClass().getSimpleName());
                            }
                        }
                    } else {
                        lang.sendMessage("invalidModule", sender, args[1]);
                    }
                }

            } else if (cmd.equalsIgnoreCase("stop")) {

                if (args.length == 1) {

                    StringBuilder sb = new StringBuilder();

                    for (Module m : Clearlag.getModules()) {
                        if (m.isEnabled()) {
                            sb.append(", ").append(m.getClass().getSimpleName());
                        }
                    }

                    lang.sendMessage("enabledModules", sender, sb.substring(2));

                } else {
                    Module mod = Clearlag.getModule(args[1]);

                    if (mod != null) {

                        if (!mod.isEnabled()) {
                            lang.sendMessage("notEnabled", sender, mod.getClass().getSimpleName());
                        } else {
                            mod.setDisabled();

                            lang.sendMessage("stoppedModule", sender, mod.getClass().getSimpleName());
                        }
                    } else {
                        lang.sendMessage("invalidModule", sender, args[1]);
                    }
                }

            } else if (cmd.equalsIgnoreCase("start")) {

                if (args.length == 1) {

                    StringBuilder sb = new StringBuilder();

                    for (Module m : Clearlag.getModules()) {
                        if (!m.isEnabled())
                            sb.append(", ").append(m.getClass().getSimpleName());
                    }

                    lang.sendMessage("startableModules", sender, sb.substring(2));

                } else {
                    Module mod = Clearlag.getModule(args[1]);

                    if (mod != null) {
                        if (mod.isEnabled()) {
                            lang.sendMessage("alreadyEnabled", sender, mod.getClass().getSimpleName());
                        } else {
                            mod.setEnabled();

                            lang.sendMessage("enabled", sender, mod.getClass().getSimpleName());
                        }
                    } else {
                        lang.sendMessage("invalidModule", sender, args[1]);
                    }
                }

            } else if (cmd.equalsIgnoreCase("list")) {

                StringBuilder events = new StringBuilder();
                StringBuilder commands = new StringBuilder();
                StringBuilder tasks = new StringBuilder();
                StringBuilder modules = new StringBuilder();

                for (Module m : Clearlag.getModules()) {

                    String s = ChatColor.DARK_GRAY + ", " + (m.isEnabled() ? ChatColor.GREEN : ChatColor.GRAY) + m.getClass().getSimpleName();

                    if (m instanceof EventModule) {
                        events.append(s);
                    } else if (m instanceof CommandModule) {
                        commands.append(s);
                    } else if (m instanceof TaskModule) {
                        tasks.append(s);
                    } else {
                        modules.append(s);
                    }
                }

                lang.sendMessage("moduleStatus", sender,
                        events.substring(4),
                        commands.substring(4),
                        tasks.substring(4),
                        modules.substring(4)
                );
            }
        }
    }
}
