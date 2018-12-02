package me.david.davidlib.spigotlib.gui;

import me.david.davidlib.datafactory.DataFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface Gui {

    void onPreOpen(Player player, InventoryData data);
    default void onPostOpen(Player player, Inventory inventory) {}

    default void onClose(Player player, InventoryData data) {}

    default String getPermission() {
        return "";
    }

    boolean onItemClick(Player player, InventoryData inventory, DataFactory data, InventoryClickEvent event);

}
