package org.crafttorch;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class KickHub {
    private final Handler plug;


    public KickHub(Handler plug) {
        this.plug = plug;
    }

    public void kickAll() {
        String svName = plug.getCustomConfig().getString("Shutdown.targetServer");
        int port = plug.getCustomConfig().getInt("Shutdown.port");
        String kickReason = plug.getCustomConfig().getString("Shutdown.kickReason");

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            plug.msg(p, kickReason);
            Handler.sendPlayerToServer(p, svName, port);
        }
    }

    public void kickShutdown() {
        kickAll();
        new BukkitRunnable() {
            public void run() {
                Bukkit.shutdown();
            }
        }.runTaskLater(plug, 40L);
    }
}
