package com.riot.villagertrades.service;

import com.riot.villagertrades.config.PluginConfig;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TradeResetService {

    private final PluginConfig config;
    private final TradeNormalizeService normalizeService;

    public TradeResetService(PluginConfig config, TradeNormalizeService normalizeService) {
        this.config = config;
        this.normalizeService = normalizeService;
    }

    public boolean canReset(Villager villager) {
        if (villager == null || !villager.isValid()) return false;
        Villager.Profession profession = villager.getProfession();
        if (profession == Villager.Profession.NONE || profession == Villager.Profession.NITWIT) return false;
        if (config.isRequireStageOneOnly()) {
            return villager.getVillagerLevel() <= 1 && villager.getVillagerExperience() <= 0;
        }
        return true;
    }

    public boolean resetTrades(Villager villager, Player player) {
        if (!config.isResetEnabled()) return false;

        Villager.Profession profession = villager.getProfession();
        if (profession == Villager.Profession.NONE || profession == Villager.Profession.NITWIT) {
            if (player != null) {
                player.sendMessage(config.getMsgNoProfession());
                if (config.isPlaySound()) player.playSound(villager.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
            return false;
        }

        if (!canReset(villager)) {
            if (player != null) {
                player.sendMessage(config.getMsgAlreadyLocked());
                if (config.isPlaySound()) player.playSound(villager.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
            return false;
        }

        villager.setVillagerExperience(0);
        villager.setVillagerLevel(1);

        List<MerchantRecipe> recipes = TradeGenerator.generateStageOneTrades(profession);
        villager.setRecipes(recipes);

        normalizeService.normalizeVillager(villager);

        if (config.isBarrierButtonEnabled()) {
            injectBarrierButton(villager);
        }

        if (player != null) {
            // Anvil sound upon trade reset as requested
            if (config.isPlaySound()) {
                player.playSound(villager.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
            }
            if (config.isSpawnParticles()) {
                villager.getWorld().spawnParticle(
                        Particle.VILLAGER_HAPPY,
                        villager.getLocation().add(0, 1.2, 0),
                        16, 0.35, 0.35, 0.35, 0.05
                );
            }
            player.sendMessage(config.getMsgResetSuccess());
        }

        return true;
    }

    /**
     * Injects the 3rd button into the villager's trade list with a BARRIER block icon.
     */
    public void injectBarrierButton(Villager villager) {
        if (!config.isBarrierButtonEnabled()) return;
        List<MerchantRecipe> recipes = new ArrayList<>(villager.getRecipes());
        recipes.removeIf(normalizeService::isResetButtonRecipe);

        ItemStack barrierItem = new ItemStack(Material.BARRIER);
        ItemMeta meta = barrierItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(config.getBarrierButtonName());
            meta.setLore(config.getBarrierButtonLore());
            barrierItem.setItemMeta(meta);
        }

        MerchantRecipe barrierRecipe = new MerchantRecipe(barrierItem, 999999);
        barrierRecipe.setIngredients(Collections.singletonList(new ItemStack(Material.EMERALD, 1)));
        barrierRecipe.setPriceMultiplier(0.0f);
        barrierRecipe.setDemand(0);
        barrierRecipe.setSpecialPrice(0);

        recipes.add(barrierRecipe);
        villager.setRecipes(recipes);
    }
}
