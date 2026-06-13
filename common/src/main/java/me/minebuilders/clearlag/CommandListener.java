package me.minebuilders.clearlag;

import me.minebuilders.clearlag.exceptions.WrongCommandArgumentException;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.Message;
import me.minebuilders.clearlag.language.messages.MessageTree;
import me.minebuilders.clearlag.modules.CommandModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandListener implements CommandExecutor, TabCompleter {

    @LanguageValue(key = "command.lagg.")
    private MessageTree lang;

    private final List<CommandModule> cmds = new ArrayList<>();

    public CommandListener() {
        Objects.requireNonNull(ClearLag.getInstance().getCommand("lagg")).setExecutor(this);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        final CommandModule enteredSubCommand = (args.length > 0 ? getCmd(args[0]) : null);

        if (enteredSubCommand == null) {
            final List<CommandModule> cmds = getUserCmds(sender);
            if (cmds.isEmpty()) {
                lang.sendMessage("nopermission", sender);
                return false;
            }

            final Message helpLineMessage = lang.getMessage("helpline");

            lang.sendMessage("header", sender);

            for (CommandModule cmd : cmds) {
                helpLineMessage.sendMessage(sender, cmd.getDisplayName(), cmd.getDescription());
            }

            lang.sendMessage("footer", sender);
        } else {
            try {
                enteredSubCommand.processCmd(sender, args);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(e.getMessage());
            } catch (WrongCommandArgumentException e) {
                e.getError().sendMessage(sender, e.getReplacables());
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }

        final String partial = args[0].toLowerCase();

        return getUserCmds(sender).stream()
                .map(CommandModule::getDisplayName)
                .filter(name -> name.startsWith(partial))
                .sorted()
                .toList();
    }

    public void addCmd(CommandModule cmd) {
        cmds.add(cmd);
    }

    private CommandModule getCmd(String s) {
        s = s.toLowerCase();
        for (CommandModule cmd : cmds) {
            if (cmd != null && cmd.getDisplayName().equals(s)) {
                return cmd;
            }
        }

        return null;
    }

    private List<CommandModule> getUserCmds(CommandSender p) {
        List<CommandModule> mod = new ArrayList<>();
        for (CommandModule cmd : cmds) {
            if (p.hasPermission("lagg." + cmd.getName())) {
                mod.add(cmd);
            }
        }

        return mod;
    }
}