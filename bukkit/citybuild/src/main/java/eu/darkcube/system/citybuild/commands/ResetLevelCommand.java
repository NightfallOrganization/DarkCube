package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.citybuild.commands.CustomHealthManager;  // Import hinzufügen
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ResetLevelCommand implements CommandExecutor {

    private LevelXPManager levelXPManager;
    private CustomHealthManager healthManager;  // Deklaration der Variable hinzufügen

    public ResetLevelCommand(LevelXPManager levelXPManager, CustomHealthManager healthManager) {
        this.levelXPManager = levelXPManager;
        this.healthManager = healthManager;  // Zuweisung der Variable hinzufügen
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            levelXPManager.resetXP(player);
            levelXPManager.resetAP(player); // AP zurücksetzen
            healthManager.resetHealth(player);  // Gesundheit zurücksetzen
            healthManager.resetMaxHealth(player);
            healthManager.resetRegen(player);  // Regeneration zurücksetzen

            // Attribute zurücksetzen
            levelXPManager.resetAttribute(player, "speed");
            levelXPManager.resetAttribute(player, "strength");
            levelXPManager.resetAttribute(player, "aroundDamage");
            levelXPManager.resetAttribute(player, "hitSpeed");

            // Spieler Geschwindigkeit und Attribute zurücksetzen
            player.setWalkSpeed(0.2f); // Standardlaufgeschwindigkeit
            player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getModifiers().forEach(player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)::removeModifier);
            player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getModifiers().forEach(player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)::removeModifier);

            player.sendMessage("§7Dein Level wurde §azurückgesetzt");  // Nachricht aktualisieren
            return true;
        } else {
            sender.sendMessage("§7Dieser Befehl kann nur von einem §aSpieler §7ausgeführt werden");
            return false;
        }
    }
}
