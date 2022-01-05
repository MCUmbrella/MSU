package vip.floatationdevice.msu.logback;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

import static vip.floatationdevice.msu.logback.I18nUtil.*;

public final class LogBack extends JavaPlugin implements Listener
{
    public static LogBack instance;
    public static Logger log;

    @Override
    public void onEnable()
    {
        log=getLogger();
        log.info("Initializing");
        instance=this;
        getServer().getPluginManager().registerEvents(this,this);
        try
        {
            ConfigManager.initialize();
            setLanguage(ConfigManager.getLanguage());
            this.setEnabled(true);
            getCommand("logback").setExecutor(new LBCommandExecutor());
            log.info("Initialization complete");
        }
        catch (Exception e)
        {
            log.severe("Initialization failed");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
        if(!ConfigManager.useMinecraftSpawnPoint() && !DataManager.isSpawnSet())
            log.warning(translate("warn-spawn-not-set"));
    }

    @Override
    public void onDisable()
    {
        // nothing to do
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e)
    {
        if(!ConfigManager.useMinecraftSpawnPoint() && !DataManager.isSpawnSet())
        {
            log.warning(translate("warn-spawn-not-set"));
            return;
        }
        Player p=e.getPlayer();
        Location loc=p.getLocation();
        Location spawn;
        try
        {
            spawn=DataManager.readSpawnLocation();
        }
        catch (Exception ex)
        {
            spawn=getServer().getWorlds().get(0).getSpawnLocation();
            log.severe(translate("err-read-spawn-fail").replace("{0}",ex.toString()));
        }
        Bukkit.getScheduler().runTaskAsynchronously(this,new Runnable(){
            @Override public void run()
            {
                try
                {
                    DataManager.writeLocation(p,loc,false);
                }
                catch(Exception ex)
                {
                    log.severe(translate("err-write-location-fail")
                            .replace("{0}",p.getName())
                            .replace("{1}",ex.toString()));
                }
            }
        });
        p.teleport(spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        if(!ConfigManager.useMinecraftSpawnPoint() && !DataManager.isSpawnSet())
        {
            log.warning(translate("warn-spawn-not-set"));
            return;
        }
        Player p=e.getPlayer();
        Location spawn;
        if(!DataManager.isRecorded(p.getUniqueId()))
        {
            try
            {
                spawn=DataManager.readSpawnLocation();
            }
            catch (Exception ex)
            {
                spawn=getServer().getWorlds().get(0).getSpawnLocation();
                log.severe(translate("err-read-spawn-fail"));
            }
            p.teleport(spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
            return;
        }
        if(ConfigManager.nofityEnabled()) p.sendMessage(translate("notify"));
    }
}
