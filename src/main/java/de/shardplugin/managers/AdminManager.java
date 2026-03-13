package de.shardplugin.managers;

import de.shardplugin.ShardPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminManager {

    private final ShardPlugin plugin;
    private final Set<String> admins = new HashSet<>();
    private File adminFile;
    private FileConfiguration adminConfig;

    public AdminManager(ShardPlugin plugin) {
        this.plugin = plugin;
        loadAdmins();
    }

    // ─── Persistence ────────────────────────────────────────────────────────────

    private void loadAdmins() {
        adminFile = new File(plugin.getDataFolder(), "admins.yml");
        if (!adminFile.exists()) {
            // First run: seed from config.yml admins list
            List<String> seedAdmins = plugin.getConfig().getStringList("settings.admins");
            admins.addAll(seedAdmins.stream().map(String::toLowerCase).toList());
            saveAdmins();
            return;
        }
        adminConfig = YamlConfiguration.loadConfiguration(adminFile);
        List<String> list = adminConfig.getStringList("admins");
        list.stream().map(String::toLowerCase).forEach(admins::add);
    }

    public void saveAdmins() {
        if (adminFile == null) adminFile = new File(plugin.getDataFolder(), "admins.yml");
        if (adminConfig == null) adminConfig = new YamlConfiguration();
        adminConfig.set("admins", admins.stream().toList());
        try {
            adminConfig.save(adminFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save admins.yml!");
        }
    }

    // ─── API ────────────────────────────────────────────────────────────────────

    public boolean isAdmin(String playerName) {
        return admins.contains(playerName.toLowerCase());
    }

    public boolean addAdmin(String playerName) {
        boolean added = admins.add(playerName.toLowerCase());
        if (added) saveAdmins();
        return added;
    }

    public boolean removeAdmin(String playerName) {
        boolean removed = admins.remove(playerName.toLowerCase());
        if (removed) saveAdmins();
        return removed;
    }

    public Set<String> getAdmins() {
        return admins;
    }

    // Super-admin check (only the one player defined in config)
    public boolean isSuperAdmin(String playerName) {
        String sa = plugin.getConfig().getString("settings.superadmin", "javakuba");
        return playerName.equalsIgnoreCase(sa);
    }
}
