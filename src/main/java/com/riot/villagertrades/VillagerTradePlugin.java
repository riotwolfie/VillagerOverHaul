package com.riot.villagertrades;

import com.riot.villagertrades.command.RemoveEnchantCommand;
import com.riot.villagertrades.config.PluginConfig;
import com.riot.villagertrades.enchant.EnchantRemoverManager;
import com.riot.villagertrades.listener.EnchantRemoverListener;
import com.riot.villagertrades.listener.TradeNormalizeListener;
import com.riot.villagertrades.listener.VillagerInteractListener;
import com.riot.villagertrades.listener.VillagerMenuListener;
import com.riot.villagertrades.service.TradeNormalizeService;
import com.riot.villagertrades.service.TradeResetService;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class VillagerTradePlugin extends JavaPlugin implements TabCompleter {

    private PluginConfig pluginConfig;
    private TradeNormalizeService normalizeService;
    private TradeResetService resetService;
    private EnchantRemoverManager enchantRemoverManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.pluginConfig = new PluginConfig(this);
        this.normalizeService = new TradeNormalizeService(pluginConfig);
        this.resetService = new TradeResetService(pluginConfig, normalizeService);
        this.enchantRemoverManager = new EnchantRemoverManager(pluginConfig);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new TradeNormalizeListener(this, pluginConfig, normalizeService, resetService), this);
        pm.registerEvents(new VillagerInteractListener(pluginConfig, normalizeService, resetService), this);
        pm.registerEvents(new VillagerMenuListener(this, pluginConfig, normalizeService, resetService), this);
        pm.registerEvents(new EnchantRemoverListener(pluginConfig, enchantRemoverManager), this);

        PluginCommand mainCmd = getCommand("villagertrades");
        if (mainCmd != null) {
            mainCmd.setExecutor(this);
            mainCmd.setTabCompleter(this);
        }

        PluginCommand removerCmd = getCommand("remove-enchant");
        if (removerCmd != null) {
            removerCmd.setExecutor(new RemoveEnchantCommand(pluginConfig, enchantRemoverManager));
        }

        getLogger().info("==================================================");
        getLogger().info(" VillagerTradeOverhaul v" + getDescription().getVersion() + " initialized.");
        getLogger()
                .info(" - 1 Emerald Trades: " + (pluginConfig.isOneEmeraldTrade() ? "ENABLED" : "DISABLED (default)"));
        getLogger().info(" - Librarian Bookless Enchantments: "
                + (pluginConfig.isLibrarianEnchantedBooksNoBook() ? "ENABLED" : "DISABLED"));
        getLogger().info(" - Stage 1 Trade Reset Menu: " + (pluginConfig.isResetEnabled() ? "ENABLED" : "DISABLED"));
        getLogger()
                .info(" - Enchant Remover (/re): " + (pluginConfig.isEnchantRemoverEnabled() ? "ENABLED" : "DISABLED"));
        getLogger().info("==================================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("VillagerTradeOverhaul disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("villagertrades.admin")) {
                sender.sendMessage(pluginConfig.getMsgNoPermission());
                return true;
            }

            pluginConfig.load();
            sender.sendMessage(pluginConfig.getMsgReloaded());
            return true;
        }

        sender.sendMessage(
                pluginConfig.getPrefix() + "§7Running §aVillagerTradeOverhaul §7v" + getDescription().getVersion());
        sender.sendMessage(pluginConfig.getPrefix() + "§7Commands:");
        sender.sendMessage(pluginConfig.getPrefix() + "§f/villagertrades reload §7- Reload configuration");
        sender.sendMessage(
                pluginConfig.getPrefix() + "§f/remove-enchant §7(or §f/re§7) - Open Enchant Remover Anvil UI");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && "reload".startsWith(args[0].toLowerCase())
                && sender.hasPermission("villagertrades.admin")) {
            return Collections.singletonList("reload");
        }
        return Collections.emptyList();
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public TradeNormalizeService getNormalizeService() {
        return normalizeService;
    }

    public TradeResetService getResetService() {
        return resetService;
    }

    public EnchantRemoverManager getEnchantRemoverManager() {
        return enchantRemoverManager;
    }
}