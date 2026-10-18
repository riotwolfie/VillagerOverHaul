package com.riot.villagertrades.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class PluginConfig {

    private final Plugin plugin;

    // Trades
    private boolean oneEmeraldTrade;
    private boolean librarianEnchantedBooksNoBook;
    private boolean sellTradesOneItem;
    private boolean lockPriceToOne;

    // Reset
    private boolean resetEnabled;
    private boolean requireStageOneOnly;

    // Barrier Button (3rd button in the trade UI)
    private boolean barrierButtonEnabled;
    private String barrierButtonName;
    private List<String> barrierButtonLore;

    // Effects
    private boolean playSound;
    private boolean spawnParticles;

    // Enchant Remover
    private boolean enchantRemoverEnabled;
    private int enchantRemoverCost;
    private boolean enchantRemoverPlaySound;
    private boolean enchantRemoverPreserveName;

    // Messages
    private String prefix;
    private String msgResetSuccess;
    private String msgAlreadyLocked;
    private String msgNoProfession;
    private String msgEnchantRemoverOpen;
    private String msgEnchantRemovedSuccess;
    private String msgReloaded;
    private String msgNoPermission;

    public PluginConfig(Plugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        // one-emerald-trade defaults to false as specified
        oneEmeraldTrade = config.getBoolean("one-emerald-trade", config.getBoolean("trades.all-trades-one-emerald", false));
        librarianEnchantedBooksNoBook = config.getBoolean("librarian-enchanted-books-no-book", config.getBoolean("trades.librarian-enchanted-books-no-book", true));
        sellTradesOneItem = config.getBoolean("sell-trades-one-item", config.getBoolean("trades.sell-trades-one-item", true));
        lockPriceToOne = config.getBoolean("lock-price-to-one", config.getBoolean("trades.lock-price-to-one", false));

        resetEnabled = config.getBoolean("reset.enabled", true);
        requireStageOneOnly = config.getBoolean("reset.require-stage-one-only", true);

        // 3rd button with barrier in villager trade list
        barrierButtonEnabled = config.getBoolean("reset.barrier-button.enabled", config.getBoolean("reset.merchant-button.enabled", true));
        barrierButtonName = color(config.getString("reset.barrier-button.item-name", config.getString("reset.merchant-button.item-name", "&c&l[ ↻ Reset Trades ]")));

        List<String> rawLore = config.getStringList("reset.barrier-button.item-lore");
        if (rawLore.isEmpty()) {
            rawLore = config.getStringList("reset.merchant-button.item-lore");
        }
        if (rawLore.isEmpty()) {
            rawLore = List.of("&7Click to instantly reroll trades!", "&7Stage 1 (Novice) only.", "&aNo workstation breaking needed.", "&e▶ Click to reset trades!");
        }
        barrierButtonLore = rawLore.stream().map(this::color).collect(Collectors.toList());

        playSound = config.getBoolean("reset.effects.play-sound", true);
        spawnParticles = config.getBoolean("reset.effects.spawn-particles", true);

        enchantRemoverEnabled = config.getBoolean("enchant-remover.enabled", true);
        enchantRemoverCost = config.getInt("enchant-remover.cost-levels", 0);
        enchantRemoverPlaySound = config.getBoolean("enchant-remover.play-sound", true);
        enchantRemoverPreserveName = config.getBoolean("enchant-remover.preserve-original-name", true);

        prefix = color(config.getString("messages.prefix", "&8[&aVillagers&8] "));
        msgResetSuccess = color(config.getString("messages.reset-success", "&a✔ Trades successfully reset!"));
        msgAlreadyLocked = color(config.getString("messages.already-locked", "&c✖ This villager's trades are locked (XP > 0) and cannot be reset."));
        msgNoProfession = color(config.getString("messages.no-profession", "&c✖ This villager has no profession."));
        msgEnchantRemoverOpen = color(config.getString("messages.enchant-remover-open", "&ePut your item in the left slot and type the enchantment you want to remove in the name box!"));
        msgEnchantRemovedSuccess = color(config.getString("messages.enchant-removed-success", "&a✔ Successfully removed &e{enchant}&a!"));
        msgReloaded = color(config.getString("messages.reloaded", "&aConfiguration reloaded successfully."));
        msgNoPermission = color(config.getString("messages.no-permission", "&cYou do not have permission to execute this command."));
    }

    public String color(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean isOneEmeraldTrade() { return oneEmeraldTrade; }
    public boolean isLibrarianEnchantedBooksNoBook() { return librarianEnchantedBooksNoBook; }
    public boolean isSellTradesOneItem() { return sellTradesOneItem; }
    public boolean isLockPriceToOne() { return lockPriceToOne; }

    public boolean isResetEnabled() { return resetEnabled; }
    public boolean isRequireStageOneOnly() { return requireStageOneOnly; }

    public boolean isBarrierButtonEnabled() { return barrierButtonEnabled; }
    public String getBarrierButtonName() { return barrierButtonName; }
    public List<String> getBarrierButtonLore() { return barrierButtonLore; }

    public boolean isPlaySound() { return playSound; }
    public boolean isSpawnParticles() { return spawnParticles; }

    public boolean isEnchantRemoverEnabled() { return enchantRemoverEnabled; }
    public int getEnchantRemoverCost() { return enchantRemoverCost; }
    public boolean isEnchantRemoverPlaySound() { return enchantRemoverPlaySound; }
    public boolean isEnchantRemoverPreserveName() { return enchantRemoverPreserveName; }

    public String getPrefix() { return prefix; }
    public String getMsgResetSuccess() { return prefix + msgResetSuccess; }
    public String getMsgAlreadyLocked() { return prefix + msgAlreadyLocked; }
    public String getMsgNoProfession() { return prefix + msgNoProfession; }
    public String getMsgEnchantRemoverOpen() { return prefix + msgEnchantRemoverOpen; }
    public String getMsgEnchantRemovedSuccess() { return prefix + msgEnchantRemovedSuccess; }
    public String getMsgReloaded() { return prefix + msgReloaded; }
    public String getMsgNoPermission() { return prefix + msgNoPermission; }
}
