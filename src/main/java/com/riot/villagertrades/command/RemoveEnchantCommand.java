package com.riot.villagertrades.command;

import com.riot.villagertrades.config.PluginConfig;
import com.riot.villagertrades.enchant.EnchantRemoverManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveEnchantCommand implements CommandExecutor {

    private final PluginConfig config;
    private final EnchantRemoverManager removerManager;

    public RemoveEnchantCommand(PluginConfig config, EnchantRemoverManager removerManager) {
        this.config = config;
        this.removerManager = removerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly in-game players can open the Enchant Remover Anvil UI.");
            return true;
        }

        if (!player.hasPermission("villagertrades.enchantremover")) {
            player.sendMessage(config.getMsgNoPermission());
            return true;
        }

        if (!config.isEnchantRemoverEnabled()) {
            player.sendMessage(config.getPrefix() + "§cThe Enchant Remover feature is currently disabled in config.");
            return true;
        }

        removerManager.startSession(player);
        return true;
    }
}
