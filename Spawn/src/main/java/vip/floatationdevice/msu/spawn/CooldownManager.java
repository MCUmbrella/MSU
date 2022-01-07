package vip.floatationdevice.msu.spawn;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager
{
    private static HashMap<UUID,CooldownThread> cooldownPlayers=new HashMap<UUID,CooldownThread>();

    public static void addCooldown(UUID u)
    {
        if(ConfigManager.getCooldownSec()<1 || Bukkit.getPlayer(u).hasPermission("spawn.nocooldown")) return;
        cooldownPlayers.put(u,new CooldownThread(u,cooldownPlayers));
        cooldownPlayers.get(u).start();
    }
    public static long getCooldownRemaining(UUID u)
    {
        return cooldownPlayers.get(u).getCooldownRemaining();
    }
    public static boolean hasCooldown(UUID u)
    {
        return cooldownPlayers.containsKey(u);
    }
}
