package de.shardplugin.listeners;

import de.shardplugin.ShardPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final ShardPlugin plugin;

    public PlayerListener(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Ensure entry exists in shardData so placeholder returns 0 instead of null
        plugin.getShardManager().getShards(event.getPlayer().getUniqueId());
    }
}
