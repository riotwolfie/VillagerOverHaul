package com.riot.villagertrades.listener;

import com.riot.villagertrades.config.PluginConfig;
import com.riot.villagertrades.enchant.EnchantRemoverManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class EnchantRemoverListener implements Listener {

    private final PluginConfig config;
    private final EnchantRemoverManager removerManager;

    public EnchantRemoverListener(PluginConfig config, EnchantRemoverManager removerManager) {
        this.config = config;
        this.removerManager = removerManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!(event.getView().getPlayer() instanceof Player player)) return;
        if (!removerManager.isSessionActive(player)) return;

        AnvilInventory inv = event.getInventory();
        ItemStack first = inv.getFirstItem();
        String renameText = inv.getRenameText();

        ItemStack result = removerManager.processEnchantRemoval(player, first, renameText);
        if (result != null) {
            inv.setRepairCost(config.getEnchantRemoverCost());
            event.setResult(result);
        } else {
            event.setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnvilClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!removerManager.isSessionActive(player)) return;

        if (!(event.getInventory() instanceof AnvilInventory inv)) return;

        // Slot 2 is the output/result slot in an anvil
        if (event.getRawSlot() == 2) {
            ItemStack result = event.getCurrentItem();
            if (result == null || result.getType() == Material.AIR) return;

            ItemStack first = inv.getFirstItem();
            if (first == null || first.getType() == Material.AIR) return;

            event.setCancelled(true);

            // Transfer result item to cursor or inventory
            if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
                player.setItemOnCursor(result.clone());
            } else {
                player.getInventory().addItem(result.clone());
            }

            // Consume input item from slot 0
            int amt = first.getAmount();
            if (amt <= 1) {
                inv.setFirstItem(null);
            } else {
                first.setAmount(amt - 1);
                inv.setFirstItem(first);
            }

            inv.setResult(null);

            if (config.isEnchantRemoverPlaySound()) {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.2f);
            }

            String removed = removerManager.getLastMatchedEnchantName(player);
            if (removed != null) {
                player.sendMessage(config.getMsgEnchantRemovedSuccess().replace("{enchant}", removed));
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            if (removerManager.isSessionActive(player)) {
                removerManager.endSession(player);
            }
        }
    }
}
