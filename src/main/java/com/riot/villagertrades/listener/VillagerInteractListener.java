package com.riot.villagertrades.listener;

import com.riot.villagertrades.config.PluginConfig;
import com.riot.villagertrades.service.TradeNormalizeService;
import com.riot.villagertrades.service.TradeResetService;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class VillagerInteractListener implements Listener {

    private final PluginConfig config;
    private final TradeNormalizeService normalizeService;
    private final TradeResetService resetService;

    public VillagerInteractListener(PluginConfig config,
                                    TradeNormalizeService normalizeService,
                                    TradeResetService resetService) {
        this.config = config;
        this.normalizeService = normalizeService;
        this.resetService = resetService;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (!(event.getRightClicked() instanceof Villager villager)) return;

        Player player = event.getPlayer();

        if (!player.hasPermission("villagertrades.use")) return;

        if (!villager.isAdult() || villager.getProfession() == Villager.Profession.NITWIT || villager.getProfession() == Villager.Profession.NONE) {
            return;
        }

        // Normalize trades on interact
        normalizeService.normalizeVillager(villager);

        boolean isStageOne = resetService.canReset(villager);

        // If stage 1, inject the 3rd Barrier button so it appears in the villager's trade list
        if (config.isBarrierButtonEnabled() && isStageOne) {
            resetService.injectBarrierButton(villager);
        }
    }
}
