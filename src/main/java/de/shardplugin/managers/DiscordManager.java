package de.shardplugin.managers;

import de.shardplugin.ShardPlugin;
import org.bukkit.Bukkit;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DiscordManager {

    private final ShardPlugin plugin;
    private static final ZoneId GERMAN_TZ = ZoneId.of("Europe/Berlin");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public enum LogAction { GIVE, ADMIN_REMOVE }

    public DiscordManager(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    // ─── Shard Action Log ───────────────────────────────────────────────────────

    public void logAction(LogAction action, String executor, String target, long amount) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ZonedDateTime now = ZonedDateTime.now(GERMAN_TZ);
                String date = now.format(DATE_FMT);
                String time = now.format(TIME_FMT);

                String title, description;
                int color;

                switch (action) {
                    case GIVE:
                        title = "💎 Shards Given";
                        description = "**" + executor + "** gave **" + String.format("%,d", amount) + " Shards** to **" + target + "**";
                        color = 0x00D26A; // green
                        break;
                    case ADMIN_REMOVE:
                        title = "🗑️ Shards Removed";
                        description = "**" + executor + "** removed **" + String.format("%,d", amount) + " Shards** from **" + target + "**";
                        color = 0xED4245; // red
                        break;
                    default: return;
                }

                String payload = buildShardEmbed(title, description, color, executor, target, amount, date, time);
                sendWebhook(payload);
            } catch (Exception e) {
                plugin.getLogger().warning("Discord log failed: " + e.getMessage());
            }
        });
    }

    // ─── Admin List Change Log ──────────────────────────────────────────────────

    public void logAdminChange(String superAdmin, String targetPlayer, boolean added) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ZonedDateTime now = ZonedDateTime.now(GERMAN_TZ);
                String date = now.format(DATE_FMT);
                String time = now.format(TIME_FMT);

                String title = added ? "🛡️ Admin Added" : "🚫 Admin Removed";
                String description = added
                        ? "**" + superAdmin + "** granted admin to **" + targetPlayer + "**"
                        : "**" + superAdmin + "** revoked admin from **" + targetPlayer + "**";
                int color = added ? 0x5865F2 : 0xFEE75C;

                String payload = "{"
                        + "\"embeds\":[{"
                        + "\"title\":\"" + escapeJson(title) + "\","
                        + "\"description\":\"" + escapeJson(description) + "\","
                        + "\"color\":" + color + ","
                        + "\"fields\":["
                        + "{\"name\":\"👑 Superadmin\",\"value\":\"" + escapeJson(superAdmin) + "\",\"inline\":true},"
                        + "{\"name\":\"🎯 Player\",\"value\":\"" + escapeJson(targetPlayer) + "\",\"inline\":true}"
                        + "],"
                        + "\"footer\":{\"text\":\"📅 " + date + "  ⏰ " + time + " (German Time)\"}"
                        + "}]}";

                sendWebhook(payload);
            } catch (Exception e) {
                plugin.getLogger().warning("Discord admin-change log failed: " + e.getMessage());
            }
        });
    }

    // ─── Reload Log ─────────────────────────────────────────────────────────────

    public void logReload(String executor) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ZonedDateTime now = ZonedDateTime.now(GERMAN_TZ);
                String payload = "{"
                        + "\"embeds\":[{"
                        + "\"title\":\"🔄 Plugin Reloaded\","
                        + "\"description\":\"**" + escapeJson(executor) + "** reloaded the ShardPlugin.\","
                        + "\"color\":16776960,"
                        + "\"footer\":{\"text\":\"📅 " + now.format(DATE_FMT) + "  ⏰ " + now.format(TIME_FMT) + " (German Time)\"}"
                        + "}]}";
                sendWebhook(payload);
            } catch (Exception e) {
                plugin.getLogger().warning("Discord reload log failed: " + e.getMessage());
            }
        });
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private String buildShardEmbed(String title, String description, int color,
                                    String executor, String target, long amount,
                                    String date, String time) {
        return "{"
                + "\"embeds\":[{"
                + "\"title\":\"" + escapeJson(title) + "\","
                + "\"description\":\"" + escapeJson(description) + "\","
                + "\"color\":" + color + ","
                + "\"fields\":["
                + "{\"name\":\"👤 Executor\",\"value\":\"" + escapeJson(executor) + "\",\"inline\":true},"
                + "{\"name\":\"🎯 Target\",\"value\":\"" + escapeJson(target) + "\",\"inline\":true},"
                + "{\"name\":\"💎 Amount\",\"value\":\"" + String.format("%,d", amount) + "\",\"inline\":true}"
                + "],"
                + "\"footer\":{\"text\":\"📅 " + date + "  ⏰ " + time + " (German Time)\"}"
                + "}]}";
    }

    private void sendWebhook(String payload) throws Exception {
        String webhookUrl = plugin.getConfig().getString("discord.webhook-url");
        if (webhookUrl == null || webhookUrl.isEmpty()) return;

        URL url = new URL(webhookUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", "ShardPlugin/2.0");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();
        if (code != 200 && code != 204) {
            plugin.getLogger().warning("Discord webhook returned HTTP " + code);
        }
        conn.disconnect();
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
