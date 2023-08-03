package eu.darkcube.system.citybuild.commands;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Sound;
import java.util.UUID;


public class AttributeCommand implements CommandExecutor, Listener {

    private final JavaPlugin plugin;
    private final CustomHealthManager healthManager;

    public AttributeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.healthManager = new CustomHealthManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            Inventory chestInventory = Bukkit.createInventory(null, 45, "§f\uDAFF\uDFEFḋ");

            // Create the firework stars with specific custom model data and place them in the specified slots
            int[] customModelData = {7, 8, 9, 10, 11, 12};
            int[] slots = {12, 14, 20, 24, 30, 32};
            String[] names = {"§7Defense", "§7Strength", "§7Health", "§7Speed", "§7Around Damage", "§7Hit Speed"};

            for (int i = 0; i < customModelData.length; i++) {
                ItemStack item = new ItemStack(Material.FIREWORK_STAR);
                ItemMeta meta = item.getItemMeta();
                meta.setCustomModelData(customModelData[i]);
                meta.setDisplayName(names[i]);
                item.setItemMeta(meta);
                chestInventory.setItem(slots[i], item);
            }

            // Create a player's head and place it in the slot 23
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
            skullMeta.setOwningPlayer(player);
            skullMeta.setDisplayName("§d" + player.getName());
            playerHead.setItemMeta(skullMeta);
            chestInventory.setItem(22, playerHead);  // Slot 23, index starts from 0

            player.openInventory(chestInventory);
            return true;
        } else {
            sender.sendMessage("§7Dieser Befehl kann nur von einem §aSpieler §7ausgeführt werden");
            return false;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§f\uDAFF\uDFEFḋ")) {
            event.setCancelled(true);

            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            // Check if the clicked item is null or does not have a meta (to avoid NullPointerException)
            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            String displayName = clickedItem.getItemMeta().getDisplayName();
            LevelXPManager levelManager = new LevelXPManager(plugin);
            int playerAP = levelManager.getAP(player);

            if (playerAP > 0) {
                switch (displayName) {
                    case "§7Speed":
                        double currentSpeed = levelManager.getAttribute(player, "speed");
                        levelManager.setAttribute(player, "speed", currentSpeed + 0.1f);
                        player.setWalkSpeed((float)(currentSpeed + 0.1f));
                        levelManager.addAP(player, -1);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1); // Sound abspielen
                        break;

                    case "§7Strength":
                        double currentStrength = levelManager.getAttribute(player, "strength");
                        levelManager.setAttribute(player, "strength", currentStrength + 1.0);
                        AttributeModifier strengthModifier = new AttributeModifier(UUID.randomUUID(), "StrengthModifier", currentStrength + 1.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
                        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).addModifier(strengthModifier);
                        levelManager.addAP(player, -1);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1); // Sound abspielen
                        break;

                    case "§7Hit Speed":
                        AttributeModifier hitSpeedModifier = new AttributeModifier(UUID.randomUUID(), "HitSpeedModifier", 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
                        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(hitSpeedModifier);
                        levelManager.addAP(player, -1);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1); // Sound abspielen
                        break;

                    case "§7Health":
                        healthManager.addMaxHealth(player, 1);
                        healthManager.addRegen(player, 1);  // Hier erhöhen wir auch die Regeneration
                        levelManager.addAP(player, -1);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        break;
                }
            } else {
                // Player does not have enough AP, show message
                player.sendMessage("§cDu hast nicht genug AP, um das Attribut zu erhöhen!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1); // Sound abspielen
            }
        }
    }
}
