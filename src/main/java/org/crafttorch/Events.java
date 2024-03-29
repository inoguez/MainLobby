package org.crafttorch;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Events implements Listener {
    Handler plug;
    ConfigurationSection getConfigForGUI;

    private final HashMap<String, Gui> invs;
    private final HashMap<String, ItemStack> itemSelector;
    private final HashMap<UUID, HashMap<String, Gui> > playerInvHM;
    private final HashMap<UUID, HashMap<String, ItemStack> > playerSelecHM;

    public Events(Handler plug) {
        this.plug = plug;
        this.getConfigForGUI = plug.getGuiConfig().getConfigurationSection("GUI");
        invs = new HashMap<>();
        itemSelector = new HashMap<>();
        playerInvHM = new HashMap<>();
        playerSelecHM = new HashMap<>();
    }


    public HashMap<UUID, HashMap<String, Gui>> getPlayerInvHM() {
        return playerInvHM;
    }

    public HashMap<UUID, HashMap<String, ItemStack>> getPlayerSelecHM() {
        return playerSelecHM;
    }

    public HashMap<String, Gui> getInvs() {
        return invs;
    }

    public HashMap<String, ItemStack> getItemSelector() {
        return itemSelector;
    }





    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent e){
        Player player = e.getPlayer();
        getPlayerInvHM().put(player.getUniqueId(), getInvs());
        getPlayerSelecHM().put(player.getUniqueId(), getItemSelector());
        createGuis(player);
        itemSelectorCreator(player);
        onPlayerJoinEffect(player);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        Player player = e.getPlayer();
        getPlayerInvHM().remove(player.getUniqueId());
        getPlayerSelecHM().remove(player.getUniqueId());
    }

    public void onPlayerJoinEffect(Player player){
        if (plug.getGuiConfig().getBoolean("Effects.enable")){
            List<String> effectType = plug.getGuiConfig().getStringList("Effects.type");
            for (String effect : effectType) {
                player.addPotionEffect(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(effect)), Integer.MAX_VALUE, 1, true, false));
            }
        }
    }

    public void refreshAndOpenInv(Player player , String inv){
        UUID id = player.getUniqueId();
        Gui gui = getPlayerInvHM().get(id).get(inv);
        Objects.requireNonNull(plug.getGuiConfig().getConfigurationSection("GUI." + inv + ".Items")).getKeys(false).forEach(keyI ->{
            String titleItem = getItemTitles(player, inv, keyI); //getPlaceHolder(player, plug.getCustomConfig().getString("GUI." + inv + ".Items." + keyI + ".title"));
            List<String> loreItemS = getItemLores(player, inv, keyI); //getPlaceHolders(player, plug.getCustomConfig().getStringList("GUI." + inv + ".Items." + keyI + ".lore"));
            String matItemS = plug.getGuiConfig().getString("GUI." + inv + ".Items." + keyI + ".material");
            int slotItemS = plug.getGuiConfig().getInt("GUI." + inv + ".Items." + keyI + ".slot");
            Inventory inventory = gui.getInventory();

            ItemStack itemS = gui.createItem(titleItem, Material.valueOf(matItemS), loreItemS);
            inventory.setItem(slotItemS, itemS );
            getPlayerSelecHM().get(id).put(keyI,itemS);
        });
        gui.openInventory(player);
    }

    //Inventory creation
    public void createGuis(Player player){
        UUID id = player.getUniqueId();
        ConfigurationSection getConfigForGUI = plug.getGuiConfig().getConfigurationSection("GUI");
        if (getConfigForGUI == null) return;
        getConfigForGUI.getKeys(false).forEach(key ->{
            String titleHolder = getSelectorTitle(player, key);//getPlaceHolder(player, plug.getCustomConfig().getString("GUI." + key + ".title"));
            if (!getPlayerInvHM().get(id).containsKey(key)){
                int size = plug.getGuiConfig().getInt("GUI." + key + ".size");
                String mat = plug.getGuiConfig().getString("GUI." + key + ".material_fill");
                Gui Cinv = new Gui(plug, size, titleHolder);
                if(mat != null){
                    Cinv.fillInv(getConfigForGUI, mat);
                }
                getInvs().put(key, Cinv);
            }
        });
        getPlayerInvHM().put(id, new HashMap<>(getInvs()));
        getInvs().clear();
    }

    //Selector of inventory creation
    public void itemSelectorCreator(Player player){
        Inventory invPlayer= player.getInventory();
        UUID id = player.getUniqueId();
        getConfigForGUI.getKeys(false).forEach(key -> {
            Gui gui = getPlayerInvHM().get(id).get(key);
            String titleHolder = getSelectorTitle(player, key); //getPlaceHolder(player, plug.getCustomConfig().getString("GUI." + key + ".title"));
            int slot = plug.getGuiConfig().getInt("GUI." + key + ".slot");
            if (slot != -1) {
                String mat = plug.getGuiConfig().getString("GUI." + key + ".material");
                List<String> loreHolders = getSelectorLore(player, key); //getPlaceHolders(player, plug.getCustomConfig().getStringList("GUI." + key + ".lore"));
                ItemStack item = gui.createItem(titleHolder, Material.valueOf(mat), loreHolders);
                invPlayer.setItem(slot, item);
                getItemSelector().put(key, item);
            }
        });
        getPlayerSelecHM().put(id, new HashMap<>(getItemSelector()));
        getItemSelector().clear();
    }



    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        UUID id = player.getUniqueId();
        Action a = e.getAction();
        if (a == Action.RIGHT_CLICK_BLOCK || a == Action.RIGHT_CLICK_AIR) {
            getConfigForGUI.getKeys(false).forEach(key -> {
                if (Objects.requireNonNull(player.getEquipment()).getItemInMainHand().equals(getPlayerSelecHM().get(id).get(key))) {
                    refreshAndOpenInv(player, key);
                }
            });
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        final Player player = (Player) e.getWhoClicked();
        UUID id = player.getUniqueId();
        getConfigForGUI.getKeys(false).forEach(key -> {
            boolean isGui = e.getInventory().equals(getPlayerInvHM().get(id).get(key).getInventory()) ;
            final ItemStack clickedItem = e.getCurrentItem();
            ItemStack selector = getPlayerSelecHM().get(id).get(key);

            if (Objects.equals(clickedItem, selector)){
                e.setCancelled(true);
            }
            if (!isGui || clickedItem == null || clickedItem.getType().isAir()) return;
            e.setCancelled(true);

            inventoryActions(player, key, clickedItem);
        });
    }

    public void inventoryActions(Player player, String Gui, ItemStack clickedItem){
        UUID id = player.getUniqueId();
        ConfigurationSection section = plug.getGuiConfig().getConfigurationSection("GUI." + Gui + ".Items");
        assert section != null;
        section.getKeys(false).forEach(keyI -> {
            if (Objects.equals(clickedItem, getPlayerSelecHM().get(id).get(keyI))) {
                String action = section.getString(keyI + ".action");
                assert action != null;
                String actionName = action.substring(action.indexOf("[") + 1, action.indexOf("]"));

                switch (actionName) {
                    case "BUNGEE":
                        String sv = action.contains(" ") ? action.split(" ", 2)[1] : "";
                        String[] parts = sv.split(":");
                        player.closeInventory();
                        Handler.sendPlayerToServer(player, parts[0], Integer.parseInt(parts[1]));
                        break;
                    case "COMMAND":
                        String cm = action.contains(" ") ? action.split(" ", 2)[1] : "";
                        player.performCommand(cm);
                        break;
                    case "CLOSE":
                        player.closeInventory();
                        break;
                    case "GUI":
                        String gui = action.contains(" ") ? action.split(" ", 2)[1] : "";
                        if (getPlayerInvHM().get(id).get(gui) == null) {plug.msg(player, "&cInventory dont exist");return;}
                        if (player.getOpenInventory() instanceof Inventory) {
                            player.closeInventory();
                        }
                        refreshAndOpenInv(player, gui);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @EventHandler
    public void onPlayerPlace (BlockPlaceEvent e){
        UUID id = e.getPlayer().getUniqueId();
        getConfigForGUI.getKeys(false).forEach(key -> {
            if (e.getItemInHand().equals(getPlayerSelecHM().get(id).get(key))) {
                e.setCancelled(true);
            }
        });
    }

    @EventHandler
    public void onPlayerDrop (PlayerDropItemEvent e){
        UUID id = e.getPlayer().getUniqueId();
        Item itemDrop = e.getItemDrop();
        getConfigForGUI.getKeys(false).forEach(key -> {
            if (itemDrop.getItemStack().equals(getPlayerSelecHM().get(id).get(key))) {
                e.setCancelled(true);
            }
        });
    }
    @EventHandler
    public void onPlayerDeath (PlayerDeathEvent e){
        UUID id = e.getEntity().getUniqueId();
        Iterator<ItemStack> iterator = e.getDrops().iterator();
        getConfigForGUI.getKeys(false).forEach(key -> {
            while (iterator.hasNext()) {
                ItemStack stack = iterator.next();
                if (getPlayerSelecHM().get(id).get(key).equals(stack)) {
                    iterator.remove();
                }
            }
        });
    }

    @EventHandler
    public void onPlayerRespawn (PlayerRespawnEvent e){
        Player player = e.getPlayer();
        itemSelectorCreator(player);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (plug.getConfig().getBoolean("Anti-Void-Death.enable")) {
            Player player = event.getPlayer();
            String[] parts = Objects.requireNonNull(plug.getConfig().getString("Anti-Void-Death.cords_to_tp")).split(",");
            Location loc = new Location(player.getWorld(), Double.parseDouble(parts[0]) , Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
            if (player.getLocation().getY() <= 8) {
                player.teleport(loc);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        boolean a = plug.getConfig().getBoolean("Player.damage");
        if(a) return;
        if (e.getEntity() instanceof Player){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onHungerDeplete(FoodLevelChangeEvent e) {
        boolean a = plug.getConfig().getBoolean("Player.hunger");
        if(a) return;
        e.setCancelled(true);
        e.getEntity().setFoodLevel(20);
    }

    @EventHandler
    public void leavesDecay(LeavesDecayEvent e){
        boolean a = plug.getGuiConfig().getBoolean("leaves-Decay");
        if(a) return;
        e.setCancelled(true);
    }
    //PlaceHolders API conection
    public List<String> getPlaceHolders(Player player, List<String> strings){
        return PlaceholderAPI.setPlaceholders(player, strings);
    }

    public String getPlaceHolder(Player player, String string){
        return PlaceholderAPI.setPlaceholders(player, string);
    }
    //GLOBAL PLACEHOLDERS RETURN METHODS

    //Selector Items
    public String getSelectorTitle(Player player, String key){
        Plugin a = plug.getServer().getPluginManager().getPlugin("PlaceholderAPI");
        String t;
        if (a != null){
            t = getPlaceHolder(player, plug.getGuiConfig().getString("GUI." + key + ".title"));
            return t;
        }
        t = plug.getGuiConfig().getString("GUI." + key + ".title");
        return t;
    }

    public List<String> getSelectorLore(Player player, String key){
        Plugin a = plug.getServer().getPluginManager().getPlugin("PlaceholderAPI");
        List<String> t;
        if (a != null){
            t = getPlaceHolders(player, plug.getGuiConfig().getStringList("GUI." + key + ".lore"));
            return t;
        }
        t = plug.getGuiConfig().getStringList("GUI." + key + ".lore");
        return t;
    }
    //Items in gui
    public String getItemTitles(Player player, String inv, String keyI){
        Plugin a = plug.getServer().getPluginManager().getPlugin("PlaceholderAPI");
        String t;
        if (a != null){
            t = getPlaceHolder(player, plug.getGuiConfig().getString("GUI." + inv + ".Items." + keyI + ".title"));
            return t;
        }
        t = plug.getGuiConfig().getString("GUI." + inv + ".Items." + keyI + ".title");
        return t;
    }

    public List<String> getItemLores(Player player, String inv, String keyI){
        Plugin a = plug.getServer().getPluginManager().getPlugin("PlaceholderAPI");
        List<String> t;
        if (a != null){
            t = getPlaceHolders(player, plug.getGuiConfig().getStringList("GUI." + inv + ".Items." + keyI + ".lore"));
            return t;
        }
        t = plug.getGuiConfig().getStringList("GUI." + inv + ".Items." + keyI + ".lore");
        return t;
    }
}

