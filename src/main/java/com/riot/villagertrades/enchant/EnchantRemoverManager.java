package com.riot.villagertrades.enchant;

import com.riot.villagertrades.config.PluginConfig;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EnchantRemoverManager {

    private final PluginConfig config;
    private final Set<UUID> activeSessions = new HashSet<>();
    private final Map<UUID, String> lastMatchedEnchantNames = new HashMap<>();

    public EnchantRemoverManager(PluginConfig config) {
        this.config = config;
    }

    public void startSession(Player player) {
        activeSessions.add(player.getUniqueId());
        player.openAnvil(player.getLocation(), true);
        player.sendMessage(config.getMsgEnchantRemoverOpen());
    }

    public void endSession(Player player) {
        activeSessions.remove(player.getUniqueId());
        lastMatchedEnchantNames.remove(player.getUniqueId());
    }

    public boolean isSessionActive(Player player) {
        return activeSessions.contains(player.getUniqueId());
    }

    public String getLastMatchedEnchantName(Player player) {
        return lastMatchedEnchantNames.get(player.getUniqueId());
    }

    /**
     * Prepares the result item: strips the typed enchantment while strictly preserving
     * all other enchantments and the item's original display name.
     */
    public ItemStack processEnchantRemoval(Player player, ItemStack inputItem, String renameText) {
        if (inputItem == null || inputItem.getType() == Material.AIR) return null;
        if (renameText == null || renameText.trim().isEmpty()) return null;

        Enchantment targetEnchant = EnchantmentUtil.findMatchingEnchantment(inputItem, renameText);
        if (targetEnchant == null) {
            lastMatchedEnchantNames.remove(player.getUniqueId());
            return null;
        }

        String prettyName = EnchantmentUtil.formatPrettyName(targetEnchant);
        lastMatchedEnchantNames.put(player.getUniqueId(), prettyName);

        ItemStack result = inputItem.clone();

        if (result.getType() == Material.ENCHANTED_BOOK) {
            if (result.getItemMeta() instanceof EnchantmentStorageMeta meta) {
                meta.removeStoredEnchant(targetEnchant);

                // Preserve original name so it does not adopt the typed enchantment name
                if (config.isEnchantRemoverPreserveName()) {
                    if (inputItem.hasItemMeta() && inputItem.getItemMeta().hasDisplayName()) {
                        meta.setDisplayName(inputItem.getItemMeta().getDisplayName());
                    } else {
                        meta.setDisplayName(null);
                    }
                }
                result.setItemMeta(meta);
            }
        } else {
            ItemMeta meta = result.getItemMeta();
            if (meta != null) {
                if (config.isEnchantRemoverPreserveName()) {
                    if (inputItem.hasItemMeta() && inputItem.getItemMeta().hasDisplayName()) {
                        meta.setDisplayName(inputItem.getItemMeta().getDisplayName());
                    } else {
                        meta.setDisplayName(null);
                    }
                }
                result.setItemMeta(meta);
            }
            result.removeEnchantment(targetEnchant);
        }

        return result;
    }
}
