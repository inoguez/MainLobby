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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public final class Handler extends JavaPlugin {
    private static Handler instance;
    private Items items;
    private FileConfiguration customConfig;


    @Override
    public void onEnable() {
        instance = this;
        createCustomConfig();
        // Plugin startup logic
        items = new Items(getInstance());
        getServer().getPluginManager().registerEvents(new Events(getInstance()), getInstance());
        Objects.requireNonNull(getCommand("MainLobby")).setExecutor(new Commands(getInstance()));
        Objects.requireNonNull(getCommand("shutdown")).setExecutor(new Commands(getInstance()));
        getServer().getMessenger().registerOutgoingPluginChannel(getInstance(), "BungeeCord");
        getServer().getPluginManager().registerEvents(new LaunchPad(getInstance()),getInstance());
        new Metrics(getInstance(),12844);
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[MainLobby] Started");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }



    public void createCustomConfig() {
        File customConfigFile = new File(getDataFolder(), "guis.yml");
        if (!customConfigFile.exists()) {
            boolean isDirectoryCreated = customConfigFile.getParentFile().mkdirs();
            if (isDirectoryCreated) saveResource("guis.yml", false);
        }

        customConfig= new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getCustomConfig() {
        return this.customConfig;
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
                String message = getInstance().format(getInstance().getCustomConfig().getString("SendingPlayerToSvMessage.success"));
                instance.msg(player, message + " &7[&4" + server + "&7]");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //player.sendPluginMessage(Handler.getInstance(), "BungeeCord", b.toByteArray());
        }else{
            String message = getInstance().format(getInstance().getCustomConfig().getString("SendingPlayerToSvMessage.failed"));
            instance.msg(player, message + " &7[&4" + server + "&7]");
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

    public Items getItems() {
        return items;
    }

    public static Handler getInstance() {
        return instance;
    }

    public String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    public List<String> formatList(List<String> input) {
        List<String> ret = new ArrayList<>();
        for (String line : input) ret.add(ChatColor.translateAlternateColorCodes('&', line));
        return ret;
    }

    public void msg(Player player, String message){
        player.sendMessage(format(message));
    }
}
