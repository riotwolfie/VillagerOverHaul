# VillagerTradeOverhaul 🌾✨

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20.x%20--%201.21.x-brightgreen.svg?style=flat-square)](https://papermc.io/)
[![Platform](https://img.shields.io/badge/Platform-Paper%20%7C%20Purpur%20%7C%20Spigot-blue.svg?style=flat-square)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg?style=flat-square)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg?style=flat-square)](LICENSE)

A high-performance, lag-free Spigot / Paper / Purpur plugin designed to streamline villager economics, eliminate tedious workstation break-and-replace cycles, and provide an intuitive Anvil-based Enchantment Remover.

---

## ⚡ Features

### 🔄 In-Trade Instant Trade Reset Button
- Open any Novice (Stage 1 / 0 XP) villager's trade interface.
- A 3rd trade offer displays a clean **`[ ↻ Reset Trades ]`** Barrier item.
- Clicking this trade rerolls the villager's Novice trades on the spot.
- Accompanied by authentic anvil sound effects and happy villager particles.
- **Zero workstation breaking** — completely eliminates the tedious break-and-place loop while protecting villagers from career-unlinking or AI pathfinding desyncs.

### 📚 Bookless Librarian Enchantment Trades
- Librarian enchanted book offers require **no regular book** ingredient.
- Players only need emeralds to purchase enchanted books, eliminating frustrating paper and leather grinding.

### 💎 Flexible Economy & 1-Emerald Pricing
- **Customizable 1-Emerald Trades** (`one-emerald-trade`): Toggleable global setting to force all item purchases to cost strictly 1 emerald.
- **1-Item Sell Trades** (`sell-trades-one-item`): Normalizes ingredient sell trades so players only need 1 item (e.g. 1 stick → 1 emerald) to trade.
- **Price Multiplier Locking** (`lock-price-to-one`): Locks prices to prevent dynamic inflation, demand spikes, or negative reputation penalties.

### 🔨 Anvil-Based Enchantment Remover (`/remove-enchant` or `/re`)
- Opens an intuitive Anvil disenchanting interface.
- Place any enchanted weapon, tool, armor, or enchanted book into the left slot.
- Type the name of the unwanted enchantment into the rename bar (e.g., `Bane of Arthropods`, `Curse of Binding`, `Mending`, `Sharpness`).
- The output slot immediately offers the item with that enchantment stripped.
- **Non-destructive**: Preserves custom display names, lore, durability, and all other remaining enchantments intact.

---

## 📜 Commands & Permissions

| Command | Aliases | Description | Permission | Default |
| :--- | :--- | :--- | :--- | :--- |
| `/villagertrades reload` | `/vt`, `/villagertrade` | Reloads `config.yml` settings | `villagertrades.admin` | `op` |
| `/remove-enchant` | `/re`, `/disenchant` | Opens the Anvil Enchantment Remover UI | `villagertrades.enchantremover` | `true` |
| *Trade Reset Button* | *In-GUI* | Allows clicking the in-trade Barrier reset button | `villagertrades.use` | `true` |

---

## ⚙️ Configuration (`config.yml`)

```yaml
# =====================================================================
#             VILLAGER TRADE OVERHAUL & ENCHANT REMOVER
# =====================================================================

one-emerald-trade: false
librarian-enchanted-books-no-book: true
sell-trades-one-item: true
lock-price-to-one: false

reset:
  enabled: true
  require-stage-one-only: true

  barrier-button:
    item-name: "&c&l[ ↻ Reset Trades ]"
    item-lore:
      - "&7Click to instantly reroll trades!"
      - "&7Stage 1 (Novice) only."
      - "&aNo workstation breaking needed."
      - "&e▶ Click to reset trades!"

  effects:
    play-sound: true
    spawn-particles: true

enchant-remover:
  enabled: true
  cost-levels: 0
  play-sound: true
  preserve-original-name: true

messages:
  prefix: "&8[&aVillagers&8] "
  reset-success: "&a✔ Trades successfully reset!"
  already-locked: "&c✖ This villager's trades are locked (XP > 0) and cannot be reset."
  no-profession: "&c✖ This villager has no profession."
  enchant-remover-open: "&ePut your item in the left slot and type the enchantment you want to remove in the name box!"
  enchant-removed-success: "&a✔ Successfully removed &e{enchant}&a!"
  reloaded: "&aConfiguration reloaded successfully."
  no-permission: "&cYou do not have permission to execute this command."
