package me.minebuilders.clearlag.config.configvalues;

import org.bukkit.Sound;

public class Warning {

    private final String[] messages;
    private final Sound sound;

    public Warning(String[] messages, Sound sound) {
        this.messages = messages;
        this.sound = sound;
    }

    public String[] getMessages() {
        return messages;
    }

    public Sound getSound() {
        return sound;
    }

    public boolean hasSound() {
        return sound != null;
    }
}