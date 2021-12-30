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

public final class LogBack extends JavaPlugin implements Listener
{
    public static LogBack instance;
    LBCommandExecutor lb;

    @Override
    public void onEnable()
    {
        getLogger().info("Initializing");
        instance=this;
        getServer().getPluginManager().registerEvents(this,this);
        try
        {
            ConfigManager.initalize();
            I18nUtil.setLanguage(ConfigManager.getLanguage());
            this.setEnabled(true);
            lb=new LBCommandExecutor();
            getCommand("logback").setExecutor(lb);
            getLogger().info("Initialization complete");
        }
        catch (Exception e)
        {
            getLogger().severe("Initialization failed");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable()
    {
        // nothing to do
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e)
    {
        Player p=e.getPlayer();
        Location loc=p.getLocation();
        Bukkit.getScheduler().runTaskAsynchronously(this,new Runnable(){
            @Override public void run()
            {
                try
                {
                    DataManager.writeLocation(p,loc,false);
                }
                catch(Exception ex)
                {
                    getLogger().severe("Failed to save logout location of "+p.getName()+": "+e);
                }
            }
        });
        p.teleport(DataManager.readSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        Player p=e.getPlayer();
        if(!DataManager.isRecorded(p.getUniqueId())) p.teleport(DataManager.readSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        if(ConfigManager.nofityEnabled()) p.sendMessage(I18nUtil.translate("notify"));
    }
}
