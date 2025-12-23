package me.Percyqaz.UltimateInventory;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Method;

public class ChestSortListener implements Listener
{
    UltimateInventory plugin;
    private Class<?> chestSortEventClass;
    private Method getInventoryMethod;
    private Method setUnmovableMethod;

    public ChestSortListener(UltimateInventory plugin)
    {
        this.plugin = plugin;
        
        // Use reflection to load ChestSort API at runtime (optional dependency)
        try {
            chestSortEventClass = Class.forName("de.jeff_media.chestsort.api.ChestSortEvent");
            getInventoryMethod = chestSortEventClass.getMethod("getInventory");
            setUnmovableMethod = chestSortEventClass.getMethod("setUnmovable", ItemStack.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            // ChestSort API not available - listener will be inactive
            chestSortEventClass = null;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void ChestSort(Object event)
    {
        // Only process if ChestSort API is available
        if (chestSortEventClass == null || !chestSortEventClass.isInstance(event)) {
            return;
        }
        
        try {
            NamespacedKey nbtKey = new NamespacedKey(plugin, "__shulkerbox_plugin");
            org.bukkit.inventory.Inventory inventory = (org.bukkit.inventory.Inventory) getInventoryMethod.invoke(event);
            
            for (var itemStack : inventory.getContents())
            {
                if (itemStack == null) continue;
                ItemMeta meta = itemStack.getItemMeta();
                if (meta == null) continue;
                PersistentDataContainer data = meta.getPersistentDataContainer();
                if(data.has(nbtKey, PersistentDataType.STRING))
                {
                    setUnmovableMethod.invoke(event, itemStack);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error handling ChestSort event: " + e.getMessage());
        }
    }

}
