//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.darkcube.system.citybuild.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class AttributeCommand implements CommandExecutor, Listener {
    private final JavaPlugin plugin;
    private NamespacedKey speedKey;
    private NamespacedKey strengthKey;
    private NamespacedKey hitSpeedKey;
    private NamespacedKey healthKey;
    private NamespacedKey aroundDamageKey;
    private NamespacedKey defenseKey;
    private CustomHealthManager healthManager;

    public AttributeCommand(JavaPlugin plugin, CustomHealthManager healthManager) {
        this.plugin = plugin;// 35
        this.healthManager = healthManager;// 36
        this.speedKey = new NamespacedKey(plugin, "SpeedKey");// 37
        this.strengthKey = new NamespacedKey(plugin, "StrengthKey");// 38
        this.hitSpeedKey = new NamespacedKey(plugin, "HitSpeedKey");// 39
        this.healthKey = new NamespacedKey(plugin, "HealthKey");// 40
        this.aroundDamageKey = new NamespacedKey(plugin, "AroundDamageKey");// 41
        this.defenseKey = new NamespacedKey(plugin, "DefenseKey");// 42
    }// 43

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {// 47
            Inventory chestInventory = Bukkit.createInventory(null, 45, "§f\udaff\udfefḋ");// 50
            int[] customModelData = new int[]{7, 8, 9, 10, 11, 12};// 53
            int[] slots = new int[]{12, 14, 20, 24, 30, 32};// 54
            String[] names = new String[]{"§dDefense", "§dStrength", "§dHealth", "§dSpeed", "§dAround Damage", "§dHit Speed"};// 55

            for (int i = 0; i < customModelData.length; ++i) {// 57
                ItemStack item = new ItemStack(Material.FIREWORK_STAR); ItemMeta meta = item.getItemMeta();
                meta.setCustomModelData(customModelData[i]); meta.setDisplayName(names[i]);
                PersistentDataContainer dataContainer = player.getPersistentDataContainer(); int level = 0; switch (names[i]) {
                    case "§dDefense":
                        level = dataContainer.getOrDefault(this.defenseKey, PersistentDataType.INTEGER, 0); break;
                    case "§dStrength":
                        level = dataContainer.getOrDefault(this.strengthKey, PersistentDataType.INTEGER, 0); break;
                    case "§dHealth":
                        level = dataContainer.getOrDefault(this.healthKey, PersistentDataType.INTEGER, 0); break;
                    case "§dSpeed":
                        level = dataContainer.getOrDefault(this.speedKey, PersistentDataType.INTEGER, 0); break;
                    case "§dAround Damage":
                        level = (int) Math.round(dataContainer.getOrDefault(this.aroundDamageKey, PersistentDataType.DOUBLE, 0.0)); break;
                    case "§dHit Speed":
                        level = dataContainer.getOrDefault(this.hitSpeedKey, PersistentDataType.INTEGER, 0);
                }

                List<String> lore = new ArrayList();// 87
                lore.add("§7Level: " + level);// 88
                meta.setLore(lore);// 89
                item.setItemMeta(meta);// 91
                chestInventory.setItem(slots[i], item);// 92
            }

            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);// 95
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();// 96
            skullMeta.setOwningPlayer(player);// 97
            skullMeta.setDisplayName("§d" + player.getName());// 98
            playerHead.setItemMeta(skullMeta);// 99
            chestInventory.setItem(22, playerHead);// 100
            player.openInventory(chestInventory);// 102
            return true;// 103
        } else {
            sender.sendMessage("§7Dieser Befehl kann nur von einem §aSpieler §7ausgeführt werden");// 105
            return false;// 106
        }
    }

    @EventHandler public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§f\udaff\udfefḋ")) {// 112
            event.setCancelled(true);// 113
            if (!(event.getWhoClicked() instanceof Player)) {// 115
                return;// 116
            }

            Player player = (Player) event.getWhoClicked();// 119
            ItemStack clickedItem = event.getCurrentItem();// 120
            if (clickedItem == null || !clickedItem.hasItemMeta()) {// 122
                return;// 123
            }

            String displayName = clickedItem.getItemMeta().getDisplayName();// 126
            LevelXPManager levelManager = new LevelXPManager(this.plugin);// 127
            int playerAP = levelManager.getAP(player);// 128
            if (playerAP > 0) {// 130
                PersistentDataContainer dataContainer = player.getPersistentDataContainer();// 131
                switch (displayName) {// 132
                    case "§dSpeed":
                        double currentSpeed = levelManager.getAttribute(player, "speed");// 134
                        levelManager.setAttribute(player, "speed", currentSpeed + 0.10000000149011612);// 135
                        player.setWalkSpeed((float) (currentSpeed + 0.10000000149011612));// 136
                        levelManager.addAP(player, -1);// 137
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);// 138
                        PersistentDataContainer dataContainerSpeed = player.getPersistentDataContainer();// 139
                        int currentClicksSpeed = (Integer) dataContainerSpeed.getOrDefault(this.speedKey, PersistentDataType.INTEGER, 0);// 140
                        dataContainerSpeed.set(this.speedKey, PersistentDataType.INTEGER, currentClicksSpeed + 1);// 141
                        ItemMeta metaSpeed = clickedItem.getItemMeta();// 142
                        List<String> loreSpeed = metaSpeed.getLore();// 143
                        loreSpeed.set(0, "§7Level: " + (currentClicksSpeed + 1));// 144
                        metaSpeed.setLore(loreSpeed);// 145
                        clickedItem.setItemMeta(metaSpeed);// 146
                        break;// 147
                    case "§dStrength":
                        double currentStrength = levelManager.getAttribute(player, "strength");// 150
                        levelManager.setAttribute(player, "strength", currentStrength + 1.0);// 151
                        AttributeModifier strengthModifier = new AttributeModifier(UUID.randomUUID(), "StrengthModifier", currentStrength + 1.0, Operation.ADD_NUMBER, EquipmentSlot.HAND);// 152
                        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).addModifier(strengthModifier);// 153
                        levelManager.addAP(player, -1);// 154
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);// 155
                        PersistentDataContainer dataContainerStrength = player.getPersistentDataContainer();// 156
                        int currentClicksStrength = (Integer) dataContainerStrength.getOrDefault(this.strengthKey, PersistentDataType.INTEGER, 0);// 157
                        dataContainerStrength.set(this.strengthKey, PersistentDataType.INTEGER, currentClicksStrength + 1);// 158
                        ItemMeta metaStrength = clickedItem.getItemMeta();// 159
                        List<String> loreStrength = metaStrength.getLore();// 160
                        loreStrength.set(0, "§7Level: " + (currentClicksStrength + 1));// 161
                        metaStrength.setLore(loreStrength);// 162
                        clickedItem.setItemMeta(metaStrength);// 163
                        break;// 164
                    case "§dHit Speed":
                        AttributeModifier hitSpeedModifier = new AttributeModifier(UUID.randomUUID(), "HitSpeedModifier", 1.0, Operation.ADD_NUMBER, EquipmentSlot.HAND);// 167
                        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(hitSpeedModifier);// 168
                        levelManager.addAP(player, -1);// 169
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);// 170
                        PersistentDataContainer dataContainerHitSpeed = player.getPersistentDataContainer();// 171
                        int currentClicksHitSpeed = (Integer) dataContainerHitSpeed.getOrDefault(this.hitSpeedKey, PersistentDataType.INTEGER, 0);// 172
                        dataContainerHitSpeed.set(this.hitSpeedKey, PersistentDataType.INTEGER, currentClicksHitSpeed + 1);// 173
                        ItemMeta metaHitSpeed = clickedItem.getItemMeta();// 174
                        List<String> loreHitSpeed = metaHitSpeed.getLore();// 175
                        loreHitSpeed.set(0, "§7Level: " + (currentClicksHitSpeed + 1));// 176
                        metaHitSpeed.setLore(loreHitSpeed);// 177
                        clickedItem.setItemMeta(metaHitSpeed);// 178
                        break;// 179
                    case "§dHealth":
                        this.healthManager.addMaxHealth(player, 10);// 182
                        this.healthManager.addRegen(player, 1);// 183
                        levelManager.addAP(player, -1);// 184
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);// 185
                        PersistentDataContainer dataContainerHealth = player.getPersistentDataContainer();// 186
                        int currentClicksHealth = (Integer) dataContainerHealth.getOrDefault(this.healthKey, PersistentDataType.INTEGER, 0);// 187
                        dataContainerHealth.set(this.healthKey, PersistentDataType.INTEGER, currentClicksHealth + 1);// 188
                        ItemMeta metaHealth = clickedItem.getItemMeta();// 189
                        List<String> loreHealth = metaHealth.getLore();// 190
                        loreHealth.set(0, "§7Level: " + (currentClicksHealth + 1));// 191
                        metaHealth.setLore(loreHealth);// 192
                        clickedItem.setItemMeta(metaHealth);// 193
                        break;// 194
                    case "§dAround Damage":
                        PersistentDataContainer dataContainerAroundDamage = player.getPersistentDataContainer();// 197
                        double currentAroundDamage; if (dataContainerAroundDamage.has(this.aroundDamageKey, PersistentDataType.DOUBLE)) {// 199
                        currentAroundDamage = (Double) dataContainerAroundDamage.get(this.aroundDamageKey, PersistentDataType.DOUBLE);// 200
                    } else {
                        currentAroundDamage = 0.0;// 202
                        dataContainerAroundDamage.set(this.aroundDamageKey, PersistentDataType.DOUBLE, 0.0);// 203
                    }

                        dataContainerAroundDamage.set(this.aroundDamageKey, PersistentDataType.DOUBLE, currentAroundDamage + 1.0);// 205
                        levelManager.addAP(player, -1);// 206
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);// 207
                        ItemMeta metaAroundDamage = clickedItem.getItemMeta();// 209
                        List<String> loreAroundDamage = metaAroundDamage.getLore();// 210
                        loreAroundDamage.set(0, "§7Level: " + (int) (currentAroundDamage + 1.0));// 211
                        metaAroundDamage.setLore(loreAroundDamage);// 212
                        clickedItem.setItemMeta(metaAroundDamage);// 213
                        break;// 214
                    case "§dDefense":
                        DefenseManager defenseManager = new DefenseManager((Citybuild) this.plugin);// 217
                        defenseManager.getDefense(player);// 218
                        defenseManager.addDefense(player, 1);// 219
                        levelManager.addAP(player, -1);// 220
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);// 221
                        PersistentDataContainer dataContainerDefense = player.getPersistentDataContainer();// 222
                        int currentClicksDefense = (Integer) dataContainerDefense.getOrDefault(this.defenseKey, PersistentDataType.INTEGER, 0);// 223
                        dataContainerDefense.set(this.defenseKey, PersistentDataType.INTEGER, currentClicksDefense + 1);// 224
                        ItemMeta metaAroundDefense = clickedItem.getItemMeta();// 225
                        List<String> loreAroundDefense = metaAroundDefense.getLore();// 226
                        loreAroundDefense.set(0, "§7Level: " + (currentClicksDefense + 1));// 227
                        metaAroundDefense.setLore(loreAroundDefense);// 228
                        clickedItem.setItemMeta(metaAroundDefense);// 229
                }
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);// 233
                player.sendMessage("§cDu hast nicht genug AP!");// 234
            }
        }

    }// 237
}
