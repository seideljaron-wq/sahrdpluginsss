package de.shardplugin.commands;

import de.shardplugin.ShardPlugin;
import de.shardplugin.managers.AdminManager;
import de.shardplugin.managers.DiscordManager;
import de.shardplugin.managers.ShardManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShardRemoveCommand implements CommandExecutor {

    private final ShardPlugin plugin;
    private final ShardManager shardManager;
    private final DiscordManager discordManager;
    private final AdminManager adminManager;

    public ShardRemoveCommand(ShardPlugin plugin) {
        this.plugin = plugin;
        this.shardManager = plugin.getShardManager();
        this.discordManager = plugin.getDiscordManager();
        this.adminManager = plugin.getAdminManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Must be an admin (list managed by javakuba via /shards-admin)
        boolean isAdmin = sender.hasPermission("shardplugin.admin")
                || (sender instanceof Player && adminManager.isAdmin(sender.getName()));

        if (!isAdmin) {
            sender.sendMessage(shardManager.getMessage("no-permission"));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(shardManager.getMessage("remove-usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(shardManager.getMessage("player-not-found")
                    .replace("{player}", args[0]));
            return true;
        }

        long amount;
        try {
            amount = Long.parseLong(args[1]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(shardManager.getMessage("invalid-amount"));
            return true;
        }

        long currentShards = shardManager.getShards(target.getUniqueId());
        boolean success = shardManager.removeShards(target.getUniqueId(), amount);
        if (!success) {
            sender.sendMessage(shardManager.getMessage("not-enough-shards")
                    .replace("{player}", target.getName())
                    .replace("{current}", String.format("%,d", currentShards))
                    .replace("{amount}", String.format("%,d", amount)));
            return true;
        }

        String executorName = (sender instanceof Player) ? sender.getName() : "Console";

        sender.sendMessage(shardManager.getMessage("remove-success")
                .replace("{amount}", String.format("%,d", amount))
                .replace("{player}", target.getName()));

        target.sendMessage(shardManager.getMessage("remove-notify")
                .replace("{amount}", String.format("%,d", amount)));

        discordManager.logAction(DiscordManager.LogAction.ADMIN_REMOVE, executorName, target.getName(), amount);
        return true;
    }
}
