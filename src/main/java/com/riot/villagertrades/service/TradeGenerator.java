package com.riot.villagertrades.service;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TradeGenerator {

    private static final Random RANDOM = new Random();

    /**
     * Generates a pair of authentic vanilla Level 1 (Stage 1) trades for the specified profession.
     */
    public static List<MerchantRecipe> generateStageOneTrades(Villager.Profession profession) {
        List<MerchantRecipe> recipes = new ArrayList<>();

        if (profession == Villager.Profession.LIBRARIAN) {
            // Librarian Stage 1: Paper trade + (Enchanted Book or Bookshelf)
            recipes.add(createTrade(new ItemStack(Material.PAPER, 1), new ItemStack(Material.EMERALD, 1)));

            if (RANDOM.nextBoolean()) {
                // Generate a random Enchanted Book trade (Cost: 1 Emerald, Result: Enchanted Book)
                ItemStack book = createRandomEnchantedBook();
                recipes.add(createTrade(new ItemStack(Material.EMERALD, 1), book));
            } else {
                // Bookshelf trade (Cost: 1 Emerald, Result: Bookshelf)
                recipes.add(createTrade(new ItemStack(Material.EMERALD, 1), new ItemStack(Material.BOOKSHELF, 1)));
            }
        } else if (profession == Villager.Profession.ARMORER) {
            recipes.add(createTrade(new ItemStack(Material.COAL, 1), new ItemStack(Material.EMERALD, 1)));
            Material[] armor = {Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS};
            recipes.add(createTrade(new ItemStack(Material.EMERALD, 1), new ItemStack(armor[RANDOM.nextInt(armor.length)], 1)));
        } else if (profession == Villager.Profession.WEAPONSMITH) {
            recipes.add(createTrade(new ItemStack(Material.COAL, 1), new ItemStack(Material.EMERALD, 1)));
            Material[] weapons = {Material.IRON_SWORD, Material.IRON_AXE};
            recipes.add(createTrade(new ItemStack(Material.EMERALD, 1), new ItemStack(weapons[RANDOM.nextInt(weapons.length)], 1)));
        } else if (profession == Villager.Profession.TOOLSMITH) {
            recipes.add(createTrade(new ItemStack(Material.COAL, 1), new ItemStack(Material.EMERALD, 1)));
            Material[] tools = {Material.STONE_AXE, Material.STONE_SHOVEL, Material.STONE_PICKAXE, Material.STONE_HOE};
            recipes.add(createTrade(new ItemStack(Material.EMERALD, 1), new ItemStack(tools[RANDOM.nextInt(tools.length)], 1)));
        } else if (profession == Villager.Profession.FLETCHER) {
            recipes.add(createTrade(new ItemStack(Material.STICK, 1), new ItemStack(Material.EMERALD, 1)));
            recipes.add(createTrade(new ItemStack(Material.EMERALD, 1), new ItemStack(Material.ARROW, 16)));
        } else if (profession == Villager.Profession.FARMER) {
            Material[] crops = {Material.WHEAT, Material.POTATO, Material.CARROT, Material.BEETROOT};
            recipes.add(createTrade(new ItemStack(crops[RANDOM.nextInt(crops.length)], 1), new ItemStack(Material.EMERALD, 1)));
            recipes.add(createTrade(new ItemStack(Material.EMERALD, 1), new ItemStack(Material.BREAD, 6)));
        } else if (profession == Villager.Profession.CLERIC) {
            recipes.add(createTrade(new ItemStack(Material.ROTTEN_FLESH, 1), new ItemStack(Material.EMERALD, 1)));
            recipes.add(createTrade(new ItemStack(Material.EMERALD, 1), new ItemStack(Material.REDSTONE, 2)));
        } else if (profession == Villager.Profession.FISHERMAN) {
            recipes.add(createTrade(new ItemStack(Material.STRING, 1), new ItemStack(Material.EMERALD, 1)));
            recipes.add(createTrade(new ItemStack(Material.EMERALD, 1), new ItemStack(Material.COOKED_COD, 6)));
        } else if (profession == Villager.Profession.BUTCHER) {
            Material[] meats = {Material.CHICKEN, Material.PORKCHOP, Material.RABBIT};
            recipes.add(createTrade(new ItemStack(meats[RANDOM.nextInt(meats.length)], 1), new ItemStack(Material.EMERALD, 1)));
            recipes.add(createTrade(new ItemStack(Material.EMERALD, 1), new ItemStack(Material.RABBIT_STEW, 1)));
        } else if (profession == Villager.Profession.CARTOGRAPHER) {
            recipes.add(createTrade(new ItemStack(Material.PAPER, 1), new ItemStack(Material.EMERALD, 1)));
            recipes.add(createTrade(new ItemStack(Material.EMERALD, 1), new ItemStack(Material.MAP, 1)));
        } else if (profession == Villager.Profession.LEATHERWORKER) {
            recipes.add(createTrade(new ItemStack(Material.LEATHER, 1), new ItemStack(Material.EMERALD, 1)));
            Material[] leatherArmor = {Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE};
            recipes.add(createTrade(new ItemStack(Material.EMERALD, 1), new ItemStack(leatherArmor[RANDOM.nextInt(leatherArmor.length)], 1)));
        } else if (profession == Villager.Profession.MASON) {
            recipes.add(createTrade(new ItemStack(Material.CLAY_BALL, 1), new ItemStack(Material.EMERALD, 1)));
            recipes.add(createTrade(new ItemStack(Material.EMERALD, 1), new ItemStack(Material.BRICK, 10)));
        } else if (profession == Villager.Profession.SHEPHERD) {
            recipes.add(createTrade(new ItemStack(Material.WHITE_WOOL, 1), new ItemStack(Material.EMERALD, 1)));
            recipes.add(createTrade(new ItemStack(Material.EMERALD, 1), new ItemStack(Material.SHEARS, 1)));
        } else {
            // Generic fallback
            recipes.add(createTrade(new ItemStack(Material.EMERALD, 1), new ItemStack(Material.BREAD, 3)));
        }

        return recipes;
    }

    /**
     * Creates a single MerchantRecipe with 1 ingredient and locked price multiplier.
     */
    public static MerchantRecipe createTrade(ItemStack ingredient, ItemStack result) {
        MerchantRecipe recipe = new MerchantRecipe(result, 16);
        recipe.setIngredients(Collections.singletonList(ingredient));
        recipe.setPriceMultiplier(0.0f);
        recipe.setDemand(0);
        recipe.setSpecialPrice(0);
        return recipe;
    }

    /**
     * Creates an authentic random Enchanted Book with a valid enchantment and level.
     */
    public static ItemStack createRandomEnchantedBook() {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        if (meta == null) return book;

        Enchantment[] enchantments = Enchantment.values();
        List<Enchantment> valid = new ArrayList<>();
        for (Enchantment ench : enchantments) {
            // Exclude curse enchantments or invalid ones
            if (ench != null && !ench.isCursed()) {
                valid.add(ench);
            }
        }

        if (valid.isEmpty()) {
            valid.add(Enchantment.PROTECTION_ENVIRONMENTAL);
        }

        Enchantment chosen = valid.get(RANDOM.nextInt(valid.size()));
        int maxLvl = chosen.getMaxLevel();
        int lvl = maxLvl > 1 ? (1 + RANDOM.nextInt(maxLvl)) : 1;

        meta.addStoredEnchant(chosen, lvl, true);
        book.setItemMeta(meta);
        return book;
    }
}
