# 💎 ShardPlugin v2

Minecraft Spigot plugin for managing **Shards** — integrates with your existing scoreboard via **PlaceholderAPI**.

---

## ✨ Features

- 💎 Persistent shard storage per player (`shards.yml`)
- 📋 Scoreboard integration via **PlaceholderAPI** — no scoreboard conflicts
- 🛡️ Two-tier permission system (admins + superadmin)
- 🔔 Discord webhook logging (German time) for every action

---

## 📦 Commands & Permissions

| Command | Who | Description |
|---|---|---|
| `/shard-give <player> <amount>` | Ops | Give shards to a player |
| `/shard-remove <player> <amount>` | **Admins only** | Remove shards from a player |
| `/shards-admin add <player>` | **javakuba only** | Add a player to the admin list |
| `/shards-admin remove <player>` | **javakuba only** | Remove a player from the admin list |
| `/shards-admin reload` | **javakuba only** | Reload config |

---

## 📋 Scoreboard Setup

This plugin does **not** create its own scoreboard. Instead, it registers a [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) expansion.

Add these placeholders to your existing scoreboard plugin:

| Placeholder | Output |
|---|---|
| `%shards_amount%` | Raw number, e.g. `101` |
| `%shards_formatted%` | Formatted, e.g. `1,250` |

**Example** (for most scoreboard plugins):
```
⬡ SHARDS: %shards_amount%
```

---

## ⚙️ Requirements

- Spigot / Paper **1.20+**
- Java **17+**
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) *(required for scoreboard integration)*

---

## 🏗️ Building

```bash
git clone https://github.com/your-username/ShardPlugin.git
cd ShardPlugin
mvn clean package
# → target/ShardPlugin-2.0.0.jar
```

---

## 🔔 Discord Logs

| Action | Color |
|---|---|
| `/shard-give` | 🟢 Green |
| `/shard-remove` | 🔴 Red |
| Admin added | 🔵 Blue |
| Admin removed | 🟡 Yellow |
| Reload | 🟡 Yellow |

All timestamps use **German time** (`Europe/Berlin`).
