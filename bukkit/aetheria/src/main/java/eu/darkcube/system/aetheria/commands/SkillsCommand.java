/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.skills.Skill;
import eu.darkcube.system.aetheria.util.SkillManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class SkillsCommand implements CommandExecutor , Listener {

    private final String GUI_NAME = "§f\udaff\udfefḪ";
    private final int SIZE = 45;
    private SkillManager skillManager;
    private final Map<UUID, Integer> previousSlots = new HashMap<>();

    public SkillsCommand(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String inventoryName = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() != null) {
            if (inventoryName.equals(GUI_NAME)) {
                event.setCancelled(true);
                int clickedSlot = event.getSlot();
                // Überprüfe, ob der geklickte Slot 11, 15, 29 oder 33 ist
                if (clickedSlot == 11 || clickedSlot == 15 || clickedSlot == 29 || clickedSlot == 33 || clickedSlot == 13 || clickedSlot == 21 || clickedSlot == 23 || clickedSlot == 31) {
                    openSecondInventory(player, clickedSlot);
                }
            } else if (inventoryName.equals("§f\udaff\udfefḩ") || inventoryName.equals("§f\udaff\udfefḫ")) {
                event.setCancelled(true);
                int clickedSlot = event.getSlot();
                int previousSlot = previousSlots.get(player.getUniqueId());
                int skillSlot = getSkillSlot(previousSlot);
                ItemStack clickedItem = event.getCurrentItem();


                if (clickedSlot == 0) {
                    // Wenn auf das FIREWORK_STAR-Item geklickt wird, öffne das erste Inventar
                    Inventory gui = createFirstInventory(player);
                    player.openInventory(gui);
                }

                if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                    String skillName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
                    Skill skill = skillManager.getSkillByName(skillName);
                    if (skill != null) {
                        player.getPersistentDataContainer().set(skillManager.getKeyName(skillSlot), PersistentDataType.STRING, skill.getName());
                        player.closeInventory();
                        Inventory gui = createFirstInventory(player);
                        player.openInventory(gui);
                    }
                }
            }
        }
    }



    private Inventory createFirstInventory(Player player) {
        Inventory gui = Bukkit.createInventory(null, SIZE, GUI_NAME);

        // Fill inventory with skills
        for (int i = 1; i <= 8; i++) {
            Skill skill = skillManager.getSkillFromSlot(player, i);
            ItemStack item;

            if (skill != null) {
                item = createItemForSkill(skill);
            } else {
                item = createEmptySlotItem();
            }

            // Set item in the correct slot
            int slot = getInventorySlot(i);
            gui.setItem(slot, item);
        }

        return gui;
    }

        private int getSkillSlot(int inventorySlot) {
            switch (inventorySlot) {
                case 13: return 1;
                case 21: return 2;
                case 23: return 3;
                case 31: return 4;
                case 11: return 5;
                case 15: return 6;
                case 29: return 7;
                case 33: return 8;
                default: return -1;
            }
        }


    private Inventory createSecondInventory(Player player, boolean isActive) {
        Inventory secondInventory = Bukkit.createInventory(null, 45, "§f\udaff\udfefḩ");
        PersistentDataContainer container = player.getPersistentDataContainer();
        Set<String> usedSkills = new HashSet<>();
        List<Integer> forbiddenSlots = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 26, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 18, 27);

        // Collect the skills already on a slot
        for (int i = 1; i <= 8; i++) {
            String skillName = container.get(skillManager.getKeyName(i), PersistentDataType.STRING);
            if (skillName != null) {
                usedSkills.add(skillName);
            }
        }

        // Fill second inventory with all skills not on a skill slot
        int index = 0;
        for (String skillName : skillManager.getSkillMap().keySet()) {
            if (!usedSkills.contains(skillName)) {
                Skill skill = isActive ? skillManager.getActiveSkillByName(skillName) : skillManager.getPassiveSkillByName(skillName);
                if (skill != null) {
                    while (forbiddenSlots.contains(index)) {
                        index++;
                    }
                    ItemStack item = createItemForSkill(skill);
                    secondInventory.setItem(index, item);
                    index++;
                }
            }
        }

        ItemStack fireworkStar = new ItemStack(Material.FIREWORK_STAR);
        ItemMeta fireworkMeta = fireworkStar.getItemMeta();
        fireworkMeta.setCustomModelData(20);
        fireworkMeta.setDisplayName("§7Zurück");
        fireworkStar.setItemMeta(fireworkMeta);
        secondInventory.setItem(0, fireworkStar);

        // Überprüfe, ob alle Slots außer Slot 0 leer sind
        boolean allEmpty = true;
        for (int i = 1; i < secondInventory.getSize(); i++) {
            if (secondInventory.getItem(i) != null) {
                allEmpty = false;
                break;
            }
        }

        // Wenn alle Slots außer Slot 0 leer sind, ändere den Namen des Inventars
        if (allEmpty) {
            secondInventory = Bukkit.createInventory(null, 45, "§f\udaff\udfefḫ");
            secondInventory.setItem(0, fireworkStar);
        }

        return secondInventory;
    }

    private void openSecondInventory(Player player, int slot) {
        boolean isActive = slot == 13 || slot == 21 || slot == 23 || slot == 31;
        Inventory secondInventory = createSecondInventory(player, isActive);
        player.openInventory(secondInventory);
        previousSlots.put(player.getUniqueId(), slot);
    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Inventory gui = Bukkit.createInventory(null, SIZE, GUI_NAME);

            // Fill inventory with skills
            for (int i = 1; i <= 8; i++) {
                Skill skill = skillManager.getSkillFromSlot(player, i);
                ItemStack item;

                if (skill != null) {
                    item = createItemForSkill(skill);
                } else {
                    item = createEmptySlotItem();
                }

                // Set item in the correct slot
                int slot = getInventorySlot(i);
                gui.setItem(slot, item);
            }

            player.openInventory(gui);
            return true;
        }
        return false;
    }

    private ItemStack createEmptySlotItem() {
        Material material = Material.FIREWORK_STAR;
        int customModelData = 15;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Unskilled");
        meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);

        return item;
    }


    private ItemStack createItemForSkill(Skill skill) {
        Material material;
        int customModelData;
        ChatColor color;

        // Set material and custom model data based on skill name
        switch (skill.getName()) {
            case "Dash":
                material = Material.FIREWORK_STAR;
                customModelData = 14;
                color = ChatColor.AQUA;
                break;

            case "VerticalDash":
                material = Material.FIREWORK_STAR;
                customModelData = 13;
                color = ChatColor.AQUA;
                break;

            case "Attraction":
                material = Material.FIREWORK_STAR;
                customModelData = 16;
                color = ChatColor.LIGHT_PURPLE;
                break;

            case "Summoner":
                material = Material.FIREWORK_STAR;
                customModelData = 18;
                color = ChatColor.YELLOW;
                break;

            case "DamageResistance":
                material = Material.FIREWORK_STAR;
                customModelData = 19;
                color = ChatColor.RED;
                break;

            default:
                material = Material.FIREWORK_STAR;
                customModelData = 17;
                color = ChatColor.GRAY;
                break;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color + skill.getName());
        meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);

        return item;
    }

    private int getInventorySlot(int skillSlot) {
        switch (skillSlot) {
            case 1: return 13;
            case 2: return 21;
            case 3: return 23;
            case 4: return 31;
            case 5: return 11;
            case 6: return 15;
            case 7: return 29;
            case 8: return 33;
            default: return -1;
        }
    }
}
