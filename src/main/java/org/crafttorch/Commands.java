package org.crafttorch;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Commands implements CommandExecutor {
    Handler plug;
    KickHub a;
    public Commands(Handler plug) {
        this.plug = plug;
        this.a = new KickHub(plug);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, String[] args){
        if (label.equalsIgnoreCase("MainLobby")){
            if (args.length == 0){
                return false;
            }
            if (args[0].equalsIgnoreCase("reload")){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plug.getConfig().getString("reload.message"))));
                plug.createCustomConfig();
                return true;
            }
        }
        if (label.equalsIgnoreCase("shutdown")){
            a.kickShutdown();
        }
        return false;
    }
}
