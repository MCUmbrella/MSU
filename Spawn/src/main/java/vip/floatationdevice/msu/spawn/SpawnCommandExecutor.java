package vip.floatationdevice.msu.spawn;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import static vip.floatationdevice.msu.spawn.I18nUtil.translate;
import static vip.floatationdevice.msu.spawn.SpawnPointManager.*;
import static vip.floatationdevice.msu.spawn.CooldownManager.*;

public class SpawnCommandExecutor implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(translate("err-player-only"));
            return false;
        }
        else
        {
            if(sender.hasPermission("spawn.spawn"))
            {
                Player p=(Player)sender;
                if(hasCooldown(p.getUniqueId()))
                {
                    p.sendMessage(translate("err-cooldown").replace("{0}",String.valueOf(getCooldownRemaining(p.getUniqueId())/1000L)));
                    return false;
                }
                if(!ConfigManager.useMinecraftSpawnPoint() && !isSpawnPointFileExist())
                {// server has no spawn file and not using mc spawn? teleport player to mc spawn anyway
                    Spawn.log.warning(translate("warn-spawn-not-set"));
                    p.teleport(Spawn.instance.getServer().getWorlds().get(0).getSpawnLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                    addCooldown(p.getUniqueId());
                    p.sendMessage(translate("spawn-success"));
                    return true;
                }
                else
                {
                    try
                    {
                        p.teleport(readSpawnLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                        addCooldown(p.getUniqueId());
                        p.sendMessage(translate("spawn-success"));
                        return true;
                    }
                    catch(Exception e)
                    {
                        p.sendMessage(translate("err-spawn-fail"));
                        Spawn.log.severe(translate("err-spawn-fail-console")
                                .replace("{0}",p.getName())
                                .replace("{1}",e.toString()));
                        return false;
                    }
                }
            }
            else
            {
                sender.sendMessage(translate("err-permission-denied"));
                return false;
            }
        }
    }
}
