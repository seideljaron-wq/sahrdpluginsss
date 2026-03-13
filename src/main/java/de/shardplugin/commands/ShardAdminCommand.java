package de.shardplugin.commands;

import de.shardplugin.ShardPlugin;
import de.shardplugin.managers.AdminManager;
import de.shardplugin.managers.DiscordManager;
import de.shardplugin.managers.ShardManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShardAdminCommand implements CommandExecutor {

    private final ShardPlugin plugin;
    private final ShardManager shardManager;
    private final DiscordManager discordManager;
    private final AdminManager adminManager;

    public ShardAdminCommand(ShardPlugin plugin) {
        this.plugin = plugin;
        this.shardManager = plugin.getShardManager();
        this.discordManager = plugin.getDiscordManager();
        this.adminManager = plugin.getAdminManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // ── Only javakuba (superadmin) can use this command ───────────────────
        boolean isSuperAdmin = sender.hasPermission("shardplugin.superadmin")
                || (sender instanceof Player && adminManager.isSuperAdmin(sender.getName()));

        if (!isSuperAdmin) {
            sender.sendMessage(shardManager.getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        // ── /shards-admin reload ──────────────────────────────────────────────
        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(shardManager.getMessage("reload-success"));
            discordManager.logReload(sender.getName());
            return true;
        }

        // ── /shards-admin add <player> | /shards-admin remove <player> ────────
        if (args.length != 2) {
            sendUsage(sender);
            return true;
        }

        String action = args[0].toLowerCase();
        String targetName = args[1];

        if (action.equals("add")) {
            boolean added = adminManager.addAdmin(targetName);
            if (!added) {
                sender.sendMessage(shardManager.getMessage("already-admin")
                        .replace("{player}", targetName));
            } else {
                sender.sendMessage(shardManager.getMessage("admin-added")
                        .replace("{player}", targetName));
                discordManager.logAdminChange(sender.getName(), targetName, true);
            }

        } else if (action.equals("remove")) {
            boolean removed = adminManager.removeAdmin(targetName);
            if (!removed) {
                sender.sendMessage(shardManager.getMessage("not-admin")
                        .replace("{player}", targetName));
            } else {
                sender.sendMessage(shardManager.getMessage("admin-removed")
                        .replace("{player}", targetName));
                discordManager.logAdminChange(sender.getName(), targetName, false);
            }

        } else {
            sendUsage(sender);
        }

        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(shardManager.getMessage("admin-usage"));
    }
}
