package dev.freakingrpg.core;

import dev.freakingrpg.FreakingRpgPlugin;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public final class FrpgCommand implements CommandExecutor, TabCompleter {

    private final FreakingRpgPlugin plugin;

    public FrpgCommand(FreakingRpgPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(plugin.brandedMessage("Use /frpg help or /frpg reload."));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("freakingrpg.admin")) {
                sender.sendMessage(plugin.brandedMessage("You do not have permission to reload."));
                return true;
            }

            plugin.reloadConfig();
            sender.sendMessage(plugin.brandedMessage("Configuration reloaded."));
            return true;
        }

        sender.sendMessage(plugin.brandedMessage("Unknown subcommand. Use /frpg help."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("help", "reload").stream()
                .filter(option -> option.startsWith(args[0].toLowerCase()))
                .toList();
        }

        return List.of();
    }
}
