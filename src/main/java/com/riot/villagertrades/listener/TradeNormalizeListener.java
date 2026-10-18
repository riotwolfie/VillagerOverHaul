package com.riot.villagertrades.listener;

import com.riot.villagertrades.config.PluginConfig;
import com.riot.villagertrades.service.TradeNormalizeService;
import com.riot.villagertrades.service.TradeResetService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.Plugin;

public class TradeNormalizeListener implements Listener {

    private final Plugin plugin;
    private final PluginConfig config;
    private final TradeNormalizeService normalizeService;
    private final TradeResetService resetService;

    public TradeNormalizeListener(Plugin plugin, PluginConfig config,
                                  TradeNormalizeService normalizeService,
                                  TradeResetService resetService) {
        this.plugin = plugin;
        this.config = config;
        this.normalizeService = normalizeService;
        this.resetService = resetService;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAcquireTrade(VillagerAcquireTradeEvent event) {
        MerchantRecipe recipe = event.getRecipe();
        normalizeService.normalizeRecipe(recipe);
        event.setRecipe(recipe);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCareerChange(VillagerCareerChangeEvent event) {
        Villager villager = event.getEntity();

        // Schedule delayed normalization so new profession trades are updated
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (villager.isValid()) {
                normalizeService.normalizeVillager(villager);
                if (config.isBarrierButtonEnabled() && resetService.canReset(villager)) {
                    resetService.injectBarrierButton(villager);
                }
            }
        });
    }
}
