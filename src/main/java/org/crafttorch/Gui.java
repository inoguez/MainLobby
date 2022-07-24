package org.crafttorch;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;


public class Gui implements InventoryHolder {
    Handler plug;
    ConfigurationSection getConfigForGUI;
    private final Inventory inv;

    public Gui(Handler plug, int size, String title) {
        this.plug = plug;
        this.getConfigForGUI = plug.getCustomConfig().getConfigurationSection("GUI");
        this.inv = Bukkit.createInventory(null, size, title);
        getConfigForGUI.getKeys(false).forEach(key ->{
            String mat = plug.getCustomConfig().getString("GUI." + key + ".material_fill");
            for (int x = 0; x < size; x++){
                ItemStack item = plug.getItems().createItem(ChatColor.BLACK + "", Material.valueOf(mat), null);
                inv.setItem(x,item);
            }
        });
    }

    //Method createItem

    //Method message

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }
}
