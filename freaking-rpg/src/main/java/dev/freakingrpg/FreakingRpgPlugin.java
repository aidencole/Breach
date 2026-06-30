package dev.freakingrpg;

import dev.freakingrpg.core.FrpgCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class FreakingRpgPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        var command = getCommand("frpg");
        if (command != null) {
            var executor = new FrpgCommand(this);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        } else {
            getLogger().severe("Command /frpg is missing from plugin.yml.");
        }

        getLogger().info("Freaking RPG enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Freaking RPG disabled.");
    }

    public Component brandedMessage(String message) {
        return Component.text("[Freaking RPG] ", NamedTextColor.GOLD)
            .append(Component.text(message, NamedTextColor.YELLOW));
    }
}
