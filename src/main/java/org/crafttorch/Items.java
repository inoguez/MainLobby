package org.crafttorch;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Items {
    private final Handler plug;
    public Items(Handler plug) {
        this.plug = plug;
    }

    public ItemStack createItem(String name, Material mat, List<String> lore){
        ItemStack item = new ItemStack(mat, 1 );
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(plug.format(name));
        meta.setLore(plug.formatList(lore));
        item.setItemMeta(meta);
        return item;
    }
}
