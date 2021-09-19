package org.crafttorch;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Commands implements CommandExecutor {
    Handler plug = Handler.getInstance();
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, String[] args){
        if (label.equalsIgnoreCase("ctgui")){
            if (!sender.hasPermission("ctgui.reload")){
                sender.sendMessage(ChatColor.RED + "No puedes usar este comando");
                return true;
            }
            if (args.length == 0){
                sender.sendMessage(ChatColor.RED + "Uso: /ctgui reload");
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        Objects.requireNonNull(plug.getCustomConfig().getString("reload.message"))));
            }
            plug.createCustomConfig();
        }
        return false;
    }
}
