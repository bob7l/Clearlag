package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.adapters.VersionAdapter;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.exceptions.WrongCommandArgumentException;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.MessageTree;
import me.minebuilders.clearlag.modules.CommandModule;
import me.minebuilders.clearlag.statrenderers.MemoryRenderer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MemoryCmd extends CommandModule {

    @LanguageValue(key = "command.memory.")
    private MessageTree lang;

    @AutoWire
    private VersionAdapter versionAdapter;

    public MemoryCmd() {
        name = "memory";
    }

    @Override
    protected void run(Player p, String[] args) throws WrongCommandArgumentException {

        if (p.getInventory().getItemInHand() != null && p.getInventory().getItemInHand().getType() != Material.AIR)
            p.getWorld().dropItem(p.getLocation(), p.getItemInHand());

        int sampleRate = 5;

        if (args.length > 0) {

            if (!Util.isInteger(args[0]))
                throw new WrongCommandArgumentException(lang.getMessage("invalidinteger"), args[0]);

            sampleRate = Math.max(1, Integer.parseInt(args[0]));
        }

        final MapView view = Bukkit.createMap(p.getWorld());

        view.setScale(MapView.Scale.NORMAL);

        for (MapRenderer renderer : view.getRenderers())
            view.removeRenderer(renderer);

        final ItemStack mapItemStack = versionAdapter.createMapItemStack(view);

        view.addRenderer(new MemoryRenderer(p, sampleRate, mapItemStack, versionAdapter, view));

        p.setItemInHand(mapItemStack);

        lang.getMessage("message").sendMessage(p);
    }

}