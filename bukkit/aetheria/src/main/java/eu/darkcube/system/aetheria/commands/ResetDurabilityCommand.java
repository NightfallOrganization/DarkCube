package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.util.CustomSword;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ResetDurabilityCommand implements CommandExecutor {

    private final CustomSword customSword;

    public ResetDurabilityCommand(CustomSword customSword) {
        this.customSword = customSword;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDieser Befehl kann nur von einem Spieler ausgeführt werden!");
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.getPersistentDataContainer().has(customSword.durabilityKey, PersistentDataType.INTEGER)) {
                int maxDurability = customSword.getMaxDurabilityOfSword(item);
                meta.getPersistentDataContainer().set(customSword.durabilityKey, PersistentDataType.INTEGER, maxDurability);
                item.setItemMeta(meta);
                player.sendMessage("§aDurability wurde zurückgesetzt!");
                customSword.updateSwordLore(item, meta.getPersistentDataContainer().getOrDefault(customSword.itemLevelKey, PersistentDataType.INTEGER, 0));
                return true;
            }
        }

        player.sendMessage("§cHalte ein gültiges Custom Sword in der Hand, um die Durability zurückzusetzen!");
        return true;
    }
}
