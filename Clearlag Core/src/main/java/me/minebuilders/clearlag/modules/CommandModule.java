package me.minebuilders.clearlag.modules;

import me.minebuilders.clearlag.CommandListener;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.exceptions.WrongCommandArgumentException;
import me.minebuilders.clearlag.language.LanguageManager;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public abstract class CommandModule extends ClearlagModule {

    @LanguageValue(key = "command.error.wrongUsage")
    protected Message wrongUsage;

    @LanguageValue(key = "command.error.noPermission")
    private Message noPermission;

    @LanguageValue(key = "command.error.onlyForPlayers")
    private Message onlyForPlayers;

    @AutoWire
    private LanguageManager languageManager;

    @AutoWire
    private CommandListener commandListener;


    protected String displayName;

    protected String name = getClass().getSimpleName().replace("Cmd", "").toLowerCase();

    protected int argLength = 0;
    protected String usage = "";
    protected String desc = "";

    public void processCmd(CommandSender sender, String[] arg) throws WrongCommandArgumentException {

        if (!sender.hasPermission("lagg." + name))
            throw new WrongCommandArgumentException(noPermission, displayName);

        if (argLength >= arg.length)
            throw new WrongCommandArgumentException(wrongUsage, usage, displayName);

        if (arg.length >= 1)
            arg = Arrays.copyOfRange(arg, 1, arg.length);

        if (sender instanceof Player)
            run((Player) sender, arg);
        else
            run(sender, arg);
    }

    protected void run(Player player, String[] args) throws WrongCommandArgumentException {
        run(((CommandSender) player), args);
    }

    protected void run(CommandSender sender, String[] args) throws WrongCommandArgumentException {
        throw new WrongCommandArgumentException(onlyForPlayers);
    }

    @Override
    public void setEnabled() {
        super.setEnabled();

        //In case external plugins/ect try to use this class for custom commands
        if (this.getClass().getPackage().getName().startsWith("me.minebuilders.clearlag.")) {
            displayName = languageManager.getMessage("command." + name + ".name").getRawStringMessage();
            desc = languageManager.getMessage("command." + name + ".desc").getRawStringMessage();
            usage = languageManager.getMessage("command." + name + ".usage").getRawStringMessage();
        } else
            displayName = name;

        commandListener.addCmd(this);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getArgLength() {
        return argLength;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return desc;
    }

}
