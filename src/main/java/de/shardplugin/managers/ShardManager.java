package de.shardplugin.managers;

import de.shardplugin.ShardPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShardManager {

    private final ShardPlugin plugin;
    private final Map<UUID, Long> shardData = new HashMap<>();
    private File dataFile;
    private FileConfiguration dataConfig;

    public ShardManager(ShardPlugin plugin) {
        this.plugin = plugin;
        loadData();
    }

    // ─── Persistence ────────────────────────────────────────────────────────────

    private void loadData() {
        dataFile = new File(plugin.getDataFolder(), "shards.yml");
        if (!dataFile.exists()) {
            try { dataFile.createNewFile(); }
            catch (IOException e) { plugin.getLogger().severe("Could not create shards.yml!"); }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        if (dataConfig.getConfigurationSection("shards") != null) {
            for (String key : dataConfig.getConfigurationSection("shards").getKeys(false)) {
                try {
                    shardData.put(UUID.fromString(key), dataConfig.getLong("shards." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void saveData() {
        for (Map.Entry<UUID, Long> entry : shardData.entrySet()) {
            dataConfig.set("shards." + entry.getKey().toString(), entry.getValue());
        }
        try { dataConfig.save(dataFile); }
        catch (IOException e) { plugin.getLogger().severe("Could not save shards.yml!"); }
    }

    // ─── Shard Operations ───────────────────────────────────────────────────────

    public long getShards(UUID uuid) {
        return shardData.getOrDefault(uuid, 0L);
    }

    public void addShards(UUID uuid, long amount) {
        shardData.put(uuid, getShards(uuid) + amount);
        saveData();
    }

    /**
     * @return true if successful, false if not enough shards
     */
    public boolean removeShards(UUID uuid, long amount) {
        long current = getShards(uuid);
        if (current < amount) return false;
        shardData.put(uuid, current - amount);
        saveData();
        return true;
    }

    // ─── Message Helper ─────────────────────────────────────────────────────────

    public String getMessage(String key) {
        String msg = plugin.getConfig().getString("messages." + key, "&cMissing message: " + key);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
