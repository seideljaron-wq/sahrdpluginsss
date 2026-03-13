package de.shardplugin.placeholder;

import de.shardplugin.ShardPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShardPlaceholder extends PlaceholderExpansion {

    private final ShardPlugin plugin;

    public ShardPlaceholder(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "shards"; // %shards_<placeholder>%
    }

    @Override
    public @NotNull String getAuthor() {
        return "javakuba";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // stays registered after reload
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "0";

        long shards = plugin.getShardManager().getShards(player.getUniqueId());

        // %shards_amount% — raw number, e.g. 1100
        if (params.equalsIgnoreCase("amount")) {
            return String.valueOf(shards);
        }

        // %shards_formatted% — thousand separators, e.g. 1,100
        if (params.equalsIgnoreCase("formatted")) {
            return String.format("%,d", shards);
        }

        // %shards_short% — compact format, e.g. 1.1K / 2.5M
        if (params.equalsIgnoreCase("short")) {
            return toShortFormat(shards);
        }

        return null;
    }

    /**
     * Converts a number to compact format:
     *   999        → 999
     *   1100       → 1.1K
     *   1500       → 1.5K
     *   1000000    → 1.0M
     *   2500000    → 2.5M
     */
    private String toShortFormat(long value) {
        if (value < 1_000) {
            return String.valueOf(value);
        } else if (value < 1_000_000) {
            double k = value / 1_000.0;
            // Show one decimal only if it's not .0
            return (k % 1.0 == 0)
                    ? String.format("%.0fK", k)
                    : String.format("%.1fK", k);
        } else if (value < 1_000_000_000) {
            double m = value / 1_000_000.0;
            return (m % 1.0 == 0)
                    ? String.format("%.0fM", m)
                    : String.format("%.1fM", m);
        } else {
            double b = value / 1_000_000_000.0;
            return (b % 1.0 == 0)
                    ? String.format("%.0fB", b)
                    : String.format("%.1fB", b);
        }
    }
}
