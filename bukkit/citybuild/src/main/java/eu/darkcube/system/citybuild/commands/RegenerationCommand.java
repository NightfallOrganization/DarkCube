package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.citybuild.util.CustomHealthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegenerationCommand implements CommandExecutor {

    private CustomHealthManager healthManager;

    public RegenerationCommand(CustomHealthManager healthManager) {
        this.healthManager = healthManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = sender.getServer().getPlayer(args[0]);
            if (targetPlayer != null) {
                int regenToAdd;
                try {
                    regenToAdd = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("Ungültiger Regenerationswert: " + args[1]);
                    return true;
                }
                healthManager.addRegen(targetPlayer, regenToAdd);
                sender.sendMessage("Regeneration hinzugefügt zu " + targetPlayer.getName() + ": " + regenToAdd);
            } else {
                sender.sendMessage("Spieler nicht gefunden: " + args[0]);
            }
            return true;
        }
        return false;
    }
}
