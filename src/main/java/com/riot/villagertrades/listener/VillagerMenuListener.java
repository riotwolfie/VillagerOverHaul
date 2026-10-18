package com.riot.villagertrades.listener;

import com.riot.villagertrades.config.PluginConfig;
import com.riot.villagertrades.service.TradeNormalizeService;
import com.riot.villagertrades.service.TradeResetService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class VillagerMenuListener implements Listener {

    private final Plugin plugin;
    private final PluginConfig config;
    private final TradeNormalizeService normalizeService;
    private final TradeResetService resetService;
    private final Set<UUID> resettingVillagers = Collections.synchronizedSet(new HashSet<>());

    public VillagerMenuListener(Plugin plugin, PluginConfig config,
                                TradeNormalizeService normalizeService,
                                TradeResetService resetService) {
        this.plugin = plugin;
        this.config = config;
        this.normalizeService = normalizeService;
        this.resetService = resetService;
    }

    /**
     * Intercepts clicking the 3rd Barrier button on the left trade list in the villager UI.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTradeSelect(TradeSelectEvent event) {
        if (!(event.getMerchant() instanceof Villager villager)) return;
        if (!(event.getView().getPlayer() instanceof Player player)) return;

        int selectedIndex = event.getIndex();
        List<MerchantRecipe> recipes = villager.getRecipes();

        if (selectedIndex >= 0 && selectedIndex < recipes.size()) {
            MerchantRecipe recipe = recipes.get(selectedIndex);
            if (isBarrierResetRecipe(recipe)) {
                event.setCancelled(true);
                handleResetAction(villager, player);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory topInventory = event.getView().getTopInventory();

        if (topInventory instanceof MerchantInventory merchantInventory) {
            ItemStack current = event.getCurrentItem();
            ItemStack cursor = event.getCursor();

            // Cancel any cursor/movement interactions with the barrier reset button
            if (isBarrierItem(current) || isBarrierItem(cursor)) {
                event.setCancelled(true);
            }

            // Clicked directly on slot 2 (result slot) with barrier
            if (event.getRawSlot() == 2 && isBarrierItem(current)) {
                event.setCancelled(true);
                if (merchantInventory.getHolder() instanceof Villager villager) {
                    handleResetAction(villager, player);
                }
            }
        }
    }

    private void handleResetAction(Villager villager, Player player) {
        // Drop the UI immediately on this tick so the player is unhooked from the merchant container
        player.closeInventory();

        UUID villagerId = villager.getUniqueId();
        if (!resettingVillagers.add(villagerId)) {
            return; // Debounce duplicate event within the same tick
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                // Ensure container is closed before modifying villager recipes
                player.closeInventory();

                if (villager.isValid()) {
                    if (resetService.canReset(villager)) {
                        resetService.resetTrades(villager, player);
                    } else {
                        player.sendMessage(config.getMsgAlreadyLocked());
                    }
                }
            } finally {
                resettingVillagers.remove(villagerId);
            }
        });
    }

    private boolean isBarrierItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (item.getType() != Material.BARRIER && item.getType() != Material.NETHER_STAR) return false;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return false;
        return item.getItemMeta().getDisplayName().equals(config.getBarrierButtonName());
    }

    private boolean isBarrierResetRecipe(MerchantRecipe recipe) {
        if (recipe == null) return false;
        return isBarrierItem(recipe.getResult());
    }
}
