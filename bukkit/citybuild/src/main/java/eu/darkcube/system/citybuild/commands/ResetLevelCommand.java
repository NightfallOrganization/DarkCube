package eu.darkcube.system.citybuild.commands;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class ResetLevelCommand implements CommandExecutor {

    private LevelXPManager levelXPManager;
    private CustomHealthManager healthManager;
    private DefenseManager defenseManager;
    private NamespacedKey speedKey;
    private NamespacedKey strengthKey;
    private NamespacedKey hitSpeedKey;
    private NamespacedKey healthKey;
    private NamespacedKey aroundDamageKey;
    private NamespacedKey defenseKey;

    public ResetLevelCommand(LevelXPManager levelXPManager, CustomHealthManager healthManager, DefenseManager defenseManager, JavaPlugin plugin) {
        this.levelXPManager = levelXPManager;
        this.healthManager = healthManager;
        this.defenseManager = defenseManager;
        this.speedKey = new NamespacedKey(plugin, "SpeedKey");
        this.strengthKey = new NamespacedKey(plugin, "StrengthKey");
        this.hitSpeedKey = new NamespacedKey(plugin, "HitSpeedKey");
        this.healthKey = new NamespacedKey(plugin, "HealthKey");
        this.aroundDamageKey = new NamespacedKey(plugin, "AroundDamageKey");
        this.defenseKey = new NamespacedKey(plugin, "DefenseKey");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            levelXPManager.resetXP(player);
            levelXPManager.resetAP(player);
            healthManager.resetHealth(player);
            healthManager.resetMaxHealth(player);
            healthManager.resetRegen(player);
            healthManager.resetAroundDamage(player);

            levelXPManager.resetAttribute(player, "speed");
            levelXPManager.resetAttribute(player, "strength");
            levelXPManager.resetAttribute(player, "aroundDamage");
            levelXPManager.resetAttribute(player, "hitSpeed");

            defenseManager.resetDefense(player);

            player.setWalkSpeed(0.2f);
            player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getModifiers().forEach(player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)::removeModifier);
            player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getModifiers().forEach(player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)::removeModifier);

            // Attribute Keys auf 0 zurücksetzen
            PersistentDataContainer dataContainer = player.getPersistentDataContainer();
            dataContainer.set(this.speedKey, PersistentDataType.INTEGER, 0);
            dataContainer.set(this.strengthKey, PersistentDataType.INTEGER, 0);
            dataContainer.set(this.hitSpeedKey, PersistentDataType.INTEGER, 0);
            dataContainer.set(this.healthKey, PersistentDataType.INTEGER, 0);
            dataContainer.set(this.aroundDamageKey, PersistentDataType.DOUBLE, 0.0);
            dataContainer.set(this.defenseKey, PersistentDataType.INTEGER, 0);

            player.sendMessage("§7Dein Level wurde §azurückgesetzt");
            return true;
        } else {
            sender.sendMessage("§7Dieser Befehl kann nur von einem §aSpieler §7ausgeführt werden");
            return false;
        }
    }
}
