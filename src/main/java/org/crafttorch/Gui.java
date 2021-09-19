package org.crafttorch;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Gui implements InventoryHolder {
    Handler plug = Handler.getInstance();
    ConfigurationSection getConfigForGUI = plug.getCustomConfig().getConfigurationSection("GUI");
    private final Inventory inv;

    public Gui(int size, String title) {
        this.inv = Bukkit.createInventory(null, size, title);
        getConfigForGUI.getKeys(false).forEach(key ->{
            String mat = plug.getCustomConfig().getString("GUI." + key + ".material_fill");
            for (int x = 0; x < size; x++){
                ItemStack item = createItem(" ", Material.valueOf(mat), null);
                inv.setItem(x,item);
            }
        });
    }

    //Method createItem
    public static ItemStack createItem(String name, Material mat, List<String> lore){
        ItemStack item = new ItemStack(mat, 1 );
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(String.valueOf(name));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    //Method message
    public static void msg(Player player, String message){
        player.sendMessage(message);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }
}
