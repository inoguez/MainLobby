package org.crafttorch;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class LaunchPad implements Listener {
    Handler plugin;

    public LaunchPad(Handler plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerWalk(PlayerMoveEvent e){
        if(plugin.getCustomConfig().getBoolean("Launchpad.enable")){
            Player player = e.getPlayer();
            Location underBlock = player.getLocation();
            underBlock.setY(underBlock.getY() - 1);
            if(player.getLocation().getBlock().getType().equals(Material.valueOf(plugin.getCustomConfig().getString("Launchpad.top-block")))
                    && underBlock.getBlock().getType().equals(Material.valueOf(plugin.getCustomConfig().getString("Launchpad.under-block")))){
                player.setVelocity(player.getLocation().getDirection().multiply(2).setY(1));
            }
        }
    }

    @EventHandler
    public void onPlaceBlock (BlockPlaceEvent e){
        Location underBlock = e.getBlock().getLocation();
        underBlock.setY(underBlock.getY() - 1);

        if (underBlock.getBlock().getType().equals(Material.ICE)){
            e.setCancelled(true);
        }
    }
}
