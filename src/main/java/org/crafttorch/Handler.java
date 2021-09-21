package org.crafttorch;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;


public final class Handler extends JavaPlugin {
    private static Handler instance;
    private FileConfiguration customConfig;


    @Override
    public void onEnable() {
        instance = this;
        createCustomConfig();
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[HubGS] Started");
        getServer().getPluginManager().registerEvents(new Events(), this);
        Objects.requireNonNull(getCommand("HubGS")).setExecutor(new Commands());
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getPluginManager().registerEvents(new LaunchPad(this),this);
        new Metrics(this,12844);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public FileConfiguration getCustomConfig() {
        return this.customConfig;
    }

    public void createCustomConfig() {
        File customConfigFile = new File(getDataFolder(), "GUI'S.yml");
        if (!customConfigFile.exists()) {
            boolean isDirectoryCreated = customConfigFile.getParentFile().mkdirs();
            if (isDirectoryCreated) saveResource("GUI'S.yml", false);
        }

        customConfig= new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }


    public static void sendPlayerToServer(Player player, String server, int port) {
        if (getInstance().isSvOnline(port)){
            try {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                out.writeUTF("Connect");
                out.writeUTF(server);
                player.sendPluginMessage(getInstance(), "BungeeCord", b.toByteArray());
                b.close();
                out.close();
                String msg = format(getInstance().getCustomConfig().getString("SendingPlayerToSvMessage.success"));
                Gui.msg(player, msg + " §7[§4" + server + "§7]");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //player.sendPluginMessage(Handler.getInstance(), "BungeeCord", b.toByteArray());
        }else{
            String msg = format(getInstance().getCustomConfig().getString("SendingPlayerToSvMessage.failed"));
            Gui.msg(player, msg + " §7[§4" + server + "§7]");
        }
    }

    public Boolean isSvOnline(int port){
        try {
            Socket s = new Socket();
            s.connect(new InetSocketAddress(InetAddress.getLocalHost(), port), 10); //good timeout is 10-20
            s.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /*
    public void loadConfig(){
        getCustomConfig().options().copyDefaults();
        saveDefaultConfig();
        reloadConfig();
    }
    */

    public static Handler getInstance() {
        return instance;
    }

    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
