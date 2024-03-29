package org.crafttorch;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    private final Inventory inv;
    protected Handler plug;

    public Gui(Handler plug, int size, String title) {
        this.plug = plug;
        this.inv = Bukkit.createInventory(null, size, Handler.format(title));
    }

    public void fillInv(ConfigurationSection GUI, String mat){
        GUI.getKeys(false).forEach(key ->{
            for (int x = 0; x < getInventory().getSize(); x++){
                ItemStack item = createItem(ChatColor.BLACK + "", Material.matchMaterial(mat), null);
                getInventory().setItem(x,item);
            }
        });
    }
    public ItemStack createItem(String name, Material mat, List<String> lore){
        ItemStack item = new ItemStack(mat, 1 );
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Handler.format(name));
        meta.setLore(Handler.formatList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public void openInventory( Player ent) {
        ent.openInventory(getInventory());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }
}
