package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.darkessentials.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TrashCommand extends DarkCommand {

    public TrashCommand() {
        super("trash",new String[]{"mülleimer"}, builder-> builder.executes(context -> {

            Player player = context.getSource().asPlayer();
            Inventory gui = Bukkit.createInventory(player, 54, "§7Mülleimer");
            player.openInventory(gui);

            return 0;
        }));
    }

}
