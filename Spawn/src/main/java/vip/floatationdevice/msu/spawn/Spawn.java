package vip.floatationdevice.msu.spawn;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Spawn extends JavaPlugin
{
    public static Spawn instance;
    public static Logger log;

    @Override
    public void onEnable()
    {
        instance=this;
        log=getLogger();
    }

    @Override
    public void onDisable()
    {
        //TODO
    }
}
