package me.minebuilders.clearlag;

import me.minebuilders.clearlag.adapters.LatestVersionAdapter;
import me.minebuilders.clearlag.adapters.LegacyVersionAdapter;
import me.minebuilders.clearlag.adapters.VersionAdapter;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.commands.*;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.language.LanguageManager;
import me.minebuilders.clearlag.listeners.*;
import me.minebuilders.clearlag.managers.EntityManager;
import me.minebuilders.clearlag.modules.BroadcastHandler;
import me.minebuilders.clearlag.modules.Module;
import me.minebuilders.clearlag.reflection.AutoWirer;
import me.minebuilders.clearlag.tasks.*;
import me.minebuilders.clearlag.triggeredremoval.TriggerManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Clearlag extends JavaPlugin {

    @AutoWire
    private ConfigHandler config;

    private static Clearlag instance;

    private static Module[] modules;

    private final AutoWirer autoWirer = new AutoWirer();

    private VersionAdapter versionAdapter = null;

    private final long initialBootTimestamp = System.currentTimeMillis();

    @Override
    public void onEnable() {

        Clearlag.instance = this;

        versionAdapter = findVersionAdapter();

        if (versionAdapter == null) {

            Util.warning("Clearlag failed to find a valid version adapter for your version (" + Util.getRawBukkitVersion() + ")");
            Util.warning("Expect bugs, and errors with certain modules!");

        } else {

            Util.log("Using version-adapter: " + versionAdapter.getClass().getSimpleName());

            autoWirer.addWireable(versionAdapter);
        }

        autoWirer.addWireable(instance);
        autoWirer.addWireable(new ConfigHandler());
        autoWirer.addWireable(getConfig());
        autoWirer.addWireable(new CommandListener());
        autoWirer.addWireable(new HaltTask());

        modules = new Module[]{
                new BroadcastHandler(),
                new LanguageManager(),
                new EntityManager(),
                new ClearCmd(),
                new AreaCmd(),
                new CheckCmd(),
                new ChunkCmd(),
                new GcCmd(),
                new TpsCmd(),
                new KillmobsCmd(),
                new ReloadCmd(),
                new TpChunkCmd(),
                new UnloadChunksCmd(),
                new HaltCmd(),
                new AdminCmd(),
                new ProfileCmd(),
                new CheckChunkCmd(),
                new SampleMemoryCmd(),
                new TickSamplerCmd(),
                new MemoryCmd(),
                new PerformanceCmd(),
                new ChunkLimiterListener(),
                new DispenceLimitEvent(),
                new EggSpawnListener(),
                new ItemLivetimeListener(),
                new EntityAISpawnListener(),
                new FireSpreadListener(),
                new ItemMergeListener(),
                new MobLimitListener(),
                new MobSpawerListener(),
                new TNTMinecartListener(),
                new TntReduceListener(),
                new ChunkEntityLimiterListener(),
                new ChunkPerEntityLimiterListener(),
                new EntityBreedListener(),
                new ChunkOverloadListener(),
                new TPSTask(),
                new LiveTask(),
                new ClearTask(),
                new LimitTask(),
                new TPSCheckTask(),
                new RAMCheckTask(),
                new LogPurger(),
                new HopperLimitListener(),
                new TriggerManager(),
                new LagSpikeTask()
        };

        autoWirer.addWireables(modules);

        for (Object module : autoWirer.getWires()) {
            try {
                autoWirer.wireObject(module);
            } catch (Exception e) {
                Util.log("Failed to wire module: " + module.getClass().getName());
                e.printStackTrace();
            }
        }

        startModules();

        if (getConfig().getBoolean("settings.auto-update")) {
            new BukkitUpdater(getFile());
        }

        Util.log("Clearlag is now enabled!");
    }

    private VersionAdapter findVersionAdapter() {

        final List<Class<? extends VersionAdapter>> versionAdapterTypes = new ArrayList<>();

        versionAdapterTypes.add(LatestVersionAdapter.class);
        versionAdapterTypes.add(LegacyVersionAdapter.class);

        for (Class<? extends VersionAdapter> versionAdapterType : versionAdapterTypes) {

            try {

                final VersionAdapter tryingVersionAdapter = versionAdapterType.newInstance();

                if (tryingVersionAdapter.isCompatible())
                    return tryingVersionAdapter;

            } catch (Throwable ignored) { }
        }

        return null;
    }

    private void startModules() {

        Util.log("Loading modules...");

        for (Module mod : modules) {

            ConfigPath configPath = mod.getClass().getAnnotation(ConfigPath.class);

            if (configPath == null || (getConfig().get(configPath.path() + ".enabled") == null || getConfig().getBoolean(configPath.path() + ".enabled"))) {
                mod.setEnabled();
            }
        }

        Util.log("Modules enabed, loading config values");

        try {
            config.setModuleConfigValues();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Util.log("Modules have been loaded!");
    }

    @Override
    public void onDisable() {
        Util.log("Clearlag is now disabled!");
    }

    public static Clearlag getInstance() {
        return instance;
    }

    public static Module[] getModules() {
        return modules;
    }

    public static Module getModule(String name) {

        for (Module module : modules) {

            if (module.getClass().getSimpleName().equalsIgnoreCase(name)) {
                return module;
            }
        }

        return null;
    }

    public AutoWirer getAutoWirer() {
        return autoWirer;
    }

    public long getInitialBootTimestamp() {
        return initialBootTimestamp;
    }

    public VersionAdapter getVersionAdapter() {
        return versionAdapter;
    }
}
