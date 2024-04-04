package me.minebuilders.clearlag.exceptions;


import org.bukkit.ChatColor;

/**
 * @author bob7l
 */
public class FancyIllegalArgumentException extends IllegalArgumentException {

    public FancyIllegalArgumentException(String warningMessage, String invalidArguments) {
        super(ChatColor.DARK_RED + warningMessage + ChatColor.DARK_GRAY + ": " + ChatColor.RED + invalidArguments);
    }


}
