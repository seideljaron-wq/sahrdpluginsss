package de.shardplugin;

import de.shardplugin.commands.ShardAdminCommand;
import de.shardplugin.commands.ShardGiveCommand;
import de.shardplugin.commands.ShardRemoveCommand;
import de.shardplugin.listeners.PlayerListener;
import de.shardplugin.managers.AdminManager;
import de.shardplugin.managers.DiscordManager;
import de.shardplugin.managers.ShardManager;
import de.shardplugin.placeholder.ShardPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ShardPlugin extends JavaPlugin {

    private static ShardPlugin instance;
    private ShardManager shardManager;
    private DiscordManager discordManager;
    private AdminManager adminManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.adminManager    = new AdminManager(this);
        this.discordManager  = new DiscordManager(this);
        this.shardManager    = new ShardManager(this);

        // Commands
        getCommand("shard-give").setExecutor(new ShardGiveCommand(this));
        getCommand("shard-remove").setExecutor(new ShardRemoveCommand(this));
        getCommand("shards-admin").setExecutor(new ShardAdminCommand(this));

        // Listener
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // PlaceholderAPI hook (soft-depend — only registers if PAPI is present)
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ShardPlaceholder(this).register();
            getLogger().info("PlaceholderAPI found — %shards_amount% registered.");
        } else {
            getLogger().warning("PlaceholderAPI not found! The placeholder %shards_amount% will NOT work.");
            getLogger().warning("Download PlaceholderAPI: https://www.spigotmc.org/resources/placeholderapi.6245/");
        }

        getLogger().info("ShardPlugin v2 enabled!");
    }

    @Override
    public void onDisable() {
        if (shardManager != null) shardManager.saveData();
        if (adminManager  != null) adminManager.saveAdmins();
        getLogger().info("ShardPlugin disabled.");
    }

    // ─── Getters ────────────────────────────────────────────────────────────────

    public static ShardPlugin getInstance()    { return instance; }
    public ShardManager     getShardManager()  { return shardManager; }
    public DiscordManager   getDiscordManager(){ return discordManager; }
    public AdminManager     getAdminManager()  { return adminManager; }
}
