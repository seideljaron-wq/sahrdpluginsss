package de.shardplugin.commands;

import de.shardplugin.ShardPlugin;
import de.shardplugin.managers.DiscordManager;
import de.shardplugin.managers.ShardManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShardGiveCommand implements CommandExecutor {

    private final ShardPlugin plugin;
    private final ShardManager shardManager;
    private final DiscordManager discordManager;

    public ShardGiveCommand(ShardPlugin plugin) {
        this.plugin = plugin;
        this.shardManager = plugin.getShardManager();
        this.discordManager = plugin.getDiscordManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("shardplugin.give")) {
            sender.sendMessage(shardManager.getMessage("no-permission"));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(shardManager.getMessage("give-usage"));
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

        shardManager.addShards(target.getUniqueId(), amount);

        String executorName = (sender instanceof Player) ? sender.getName() : "Console";

        sender.sendMessage(shardManager.getMessage("give-success")
                .replace("{amount}", String.format("%,d", amount))
                .replace("{player}", target.getName()));

        target.sendMessage(shardManager.getMessage("give-received")
                .replace("{amount}", String.format("%,d", amount))
                .replace("{sender}", executorName));

        discordManager.logAction(DiscordManager.LogAction.GIVE, executorName, target.getName(), amount);
        return true;
    }
}
