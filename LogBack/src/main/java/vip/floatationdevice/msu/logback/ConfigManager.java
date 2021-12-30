package vip.floatationdevice.msu.logback;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager
{
    protected static YamlConfiguration cfg;
    protected static void initalize()
    {
        LogBack.instance.getLogger().info("Loading configurations");
        File cfgFile = new File(LogBack.instance.getDataFolder(), "config.yml");
        if (!cfgFile.exists()) LogBack.instance.saveResource("config.yml", false);
        cfg=YamlConfiguration.loadConfiguration(cfgFile);
        LogBack.instance.getLogger().info("Configurations loaded");
    }
    protected static String getLanguage(){return cfg.getString("language");}
    protected static boolean nofityEnabled(){return cfg.getBoolean("notify");}
    protected static boolean useServerSpawnPoint(){return cfg.getBoolean("useServerSpawnPoint");}
}
