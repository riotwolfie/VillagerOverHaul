package com.riot.villagertrades.service;

import com.riot.villagertrades.config.PluginConfig;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TradeNormalizeService {

    private final PluginConfig config;

    public TradeNormalizeService(PluginConfig config) {
        this.config = config;
    }

    /**
     * Normalizes all existing trade offers on a villager according to the rules.
     */
    public void normalizeVillager(Villager villager) {
        if (villager == null) return;
        List<MerchantRecipe> recipes = villager.getRecipes();
        if (recipes == null || recipes.isEmpty()) return;

        List<MerchantRecipe> updated = new ArrayList<>();
        for (MerchantRecipe recipe : recipes) {
            if (isResetButtonRecipe(recipe)) {
                updated.add(recipe);
                continue;
            }
            normalizeRecipe(recipe);
            updated.add(recipe);
        }

        villager.setRecipes(updated);
    }

    /**
     * Normalizes a single MerchantRecipe according to the active configuration:
     * - one-emerald-trade: if true, forces buy trades to cost 1 emerald.
     * - librarian-enchanted-books-no-book: removes the normal book ingredient.
     * - sell-trades-one-item: sets input requirement to 1 for emerald selling trades.
     */
    public void normalizeRecipe(MerchantRecipe recipe) {
        if (recipe == null) return;
        ItemStack result = recipe.getResult();
        if (result == null || result.getType() == Material.AIR) return;

        if (result.getType() == Material.ENCHANTED_BOOK) {
            if (config.isLibrarianEnchantedBooksNoBook()) {
                if (config.isOneEmeraldTrade()) {
                    recipe.setIngredients(Collections.singletonList(new ItemStack(Material.EMERALD, 1)));
                } else {
                    // Keep existing emerald price, but strip the normal book ingredient
                    ItemStack emerald = null;
                    for (ItemStack ing : recipe.getIngredients()) {
                        if (ing != null && ing.getType() == Material.EMERALD) {
                            emerald = ing.clone();
                            break;
                        }
                    }
                    if (emerald != null) {
                        recipe.setIngredients(Collections.singletonList(emerald));
                    } else {
                        recipe.setIngredients(Collections.singletonList(new ItemStack(Material.EMERALD, 1)));
                    }
                }
            } else if (config.isOneEmeraldTrade()) {
                List<ItemStack> updated = new ArrayList<>();
                updated.add(new ItemStack(Material.EMERALD, 1));
                for (ItemStack ing : recipe.getIngredients()) {
                    if (ing != null && ing.getType() != Material.EMERALD) {
                        updated.add(ing.clone());
                    }
                }
                recipe.setIngredients(updated);
            }
        } else if (result.getType() != Material.EMERALD) {
            // Selling item to player
            if (config.isOneEmeraldTrade()) {
                recipe.setIngredients(Collections.singletonList(new ItemStack(Material.EMERALD, 1)));
            }
        } else {
            // Result is Emerald (player selling items to villager)
            if (config.isSellTradesOneItem()) {
                List<ItemStack> currentIngredients = recipe.getIngredients();
                if (!currentIngredients.isEmpty() && currentIngredients.get(0) != null) {
                    ItemStack first = currentIngredients.get(0).clone();
                    first.setAmount(1);
                    recipe.setIngredients(Collections.singletonList(first));
                }
            }
        }

        // Lock price to 1 if enabled
        if (config.isLockPriceToOne() && config.isOneEmeraldTrade()) {
            recipe.setPriceMultiplier(0.0f);
            recipe.setDemand(0);
            recipe.setSpecialPrice(0);
        }
    }

    /**
     * Checks if a recipe is the 3rd Barrier reset button.
     */
    public boolean isResetButtonRecipe(MerchantRecipe recipe) {
        if (recipe == null) return false;
        ItemStack result = recipe.getResult();
        if (result == null) return false;
        if (result.getType() != Material.BARRIER && result.getType() != Material.NETHER_STAR) return false;
        if (!result.hasItemMeta() || !result.getItemMeta().hasDisplayName()) return false;
        return result.getItemMeta().getDisplayName().equals(config.getBarrierButtonName());
    }
}
