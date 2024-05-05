package building.oneblock.listener;

import building.oneblock.OneBlock;
import building.oneblock.gui.MenuEditGUI;
import building.oneblock.gui.MenuGUI;
import building.oneblock.manager.WorldSlotManager;
import building.oneblock.util.Message;
import building.oneblock.util.StartWorld;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuGUIListener implements Listener {
    private MenuGUI menuGUI;
    private MenuEditGUI menuEditGUI;
    private WorldSlotManager worldSlotManager;

    public MenuGUIListener(MenuGUI menuGUI, MenuEditGUI menuEditGUI, WorldSlotManager worldSlotManager) {
        this.menuGUI = menuGUI;
        this.menuEditGUI = menuEditGUI;
        this.worldSlotManager = worldSlotManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof Player && event.getView().getTitle().equals(menuGUI.getMenuGUITitle())) {
            event.setCancelled(true);

            int slot = event.getRawSlot();
            Player player = (Player) event.getWhoClicked();

            if (slot >= 10 && slot <= 14) {
                switch (event.getClick()) {
                    case RIGHT:
                        handleRightClick(player, slot);
                        break;
                    case LEFT:
                        handleLeftClick(player, slot);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void handleRightClick(Player player, int slot) {
        int transformedSlot = slot - 10;
        menuEditGUI.openMenuEditGUI(player);
    }

    private void handleLeftClick(Player player, int slot) {
        int transformedSlot = slot - 10;
        String worldSlot = worldSlotManager.getWorld(transformedSlot);
        World world = Bukkit.getWorld(worldSlot);
        Location location = new Location(world, 0.5, 100, 0.5);
        User user = UserAPI.instance().user(player.getUniqueId());

        if ("empty".equals(worldSlot)) {
            new StartWorld(worldSlotManager).execute(player, transformedSlot);
        } else {
            Bukkit.createWorld(new WorldCreator(worldSlot));
            player.teleportAsync(location).thenAccept(success -> {
                user.sendMessage(Message.YOUR_ONEBLOCK_WORLD_SUCCESSFUL_LOADED, worldSlot);
            });
        }

        menuGUI.closeMenuGUI(player);
    }

}
