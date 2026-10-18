package com.riot.villagertrades.enchant;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;
import java.util.Set;

public class EnchantmentUtil {

    /**
     * Finds an enchantment present on the given item that matches the user's input string.
     */
    public static Enchantment findMatchingEnchantment(ItemStack item, String query) {
        if (item == null || query == null || query.trim().isEmpty()) {
            return null;
        }

        Set<Enchantment> presentEnchants;
        if (item.getType() == Material.ENCHANTED_BOOK) {
            if (!(item.getItemMeta() instanceof EnchantmentStorageMeta meta)) return null;
            presentEnchants = meta.getStoredEnchants().keySet();
        } else {
            presentEnchants = item.getEnchantments().keySet();
        }

        if (presentEnchants.isEmpty()) {
            return null;
        }

        String cleanQuery = clean(query);
        if (cleanQuery.isEmpty()) return null;

        // Pass 1: Exact normalized match
        for (Enchantment ench : presentEnchants) {
            if (ench == null) continue;
            String key = clean(ench.getKey().getKey());
            String pretty = clean(formatPrettyName(ench));
            String legacy = clean(ench.getName());

            if (cleanQuery.equals(key) || cleanQuery.equals(pretty) || cleanQuery.equals(legacy)) {
                return ench;
            }
        }

        // Pass 2: Starts with or prefix match (if query >= 3 chars)
        if (cleanQuery.length() >= 3) {
            for (Enchantment ench : presentEnchants) {
                if (ench == null) continue;
                String key = clean(ench.getKey().getKey());
                String pretty = clean(formatPrettyName(ench));

                if (pretty.startsWith(cleanQuery) || key.startsWith(cleanQuery)) {
                    return ench;
                }
            }
        }

        // Pass 3: Substring / contains match (if query >= 4 chars)
        if (cleanQuery.length() >= 4) {
            for (Enchantment ench : presentEnchants) {
                if (ench == null) continue;
                String key = clean(ench.getKey().getKey());
                String pretty = clean(formatPrettyName(ench));

                if (pretty.contains(cleanQuery) || key.contains(cleanQuery)) {
                    return ench;
                }
            }
        }

        return null;
    }

    /**
     * Strips all non-alphanumeric characters and converts to lowercase.
     */
    public static String clean(String str) {
        if (str == null) return "";
        return str.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    /**
     * Converts names like 'bane_of_arthropods' to 'Bane of Arthropods'.
     */
    public static String formatPrettyName(Enchantment ench) {
        if (ench == null) return "";
        String raw = ench.getKey().getKey();
        String[] words = raw.split("_");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String w = words[i].toLowerCase();
            if (i > 0 && (w.equals("of") || w.equals("the"))) {
                sb.append(w).append(" ");
            } else if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
