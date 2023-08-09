package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.util.WorkbenchUtil;
import eu.darkcube.system.version.VersionSupport;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.concurrent.CircuitBreaker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class CraftingTableListener implements Listener {

    private final Citybuild citybuild;

    public CraftingTableListener(Citybuild citybuild) {
        this.citybuild = citybuild;
    }

    @EventHandler public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CRAFTING_TABLE) {
            event.setCancelled(true);  // Verhindert das normale Öffnen des Crafting Tables

            Player player = event.getPlayer();

            Component offset = Component.text(citybuild.glyphWidthManager().spacesForWidth(-39));
            Component title = offset.append(Component.text('Ḇ', NamedTextColor.WHITE));

            VersionSupport.version().provider().service(WorkbenchUtil.class).openWorkbench(player, title);
        }
    }
}
