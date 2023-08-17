package eu.darkcube.system.citybuild.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomSword implements Listener {
    private final JavaPlugin plugin;
    public final NamespacedKey itemLevelKey;
    public final NamespacedKey durabilityKey;
    public final NamespacedKey maxDurabilityKey;

    public CustomSword(JavaPlugin plugin) {
        this.plugin = plugin;
        this.itemLevelKey = new NamespacedKey(plugin, "item_level");
        this.durabilityKey = new NamespacedKey(plugin, "durability");
        this.maxDurabilityKey = new NamespacedKey(plugin, "max_durability");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private double getAttackDamageForLevel(int level) {
        return level * 2;
    }

    private void setSwordAttackDamage(ItemStack item, double damage) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            AttributeModifier damageModifier = new AttributeModifier(UUID.randomUUID(), "generic.attack_damage", damage, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damageModifier);
            item.setItemMeta(meta);
        }
    }

    public void increaseItemLevel(ItemStack item, int levelToAdd) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            int currentLevel = meta.getPersistentDataContainer().getOrDefault(itemLevelKey, PersistentDataType.INTEGER, 0);
            int newLevel = currentLevel + levelToAdd;

            meta.getPersistentDataContainer().set(itemLevelKey, PersistentDataType.INTEGER, newLevel);
            item.setItemMeta(meta);
            updateSwordLore(item, newLevel);
            setSwordAttackDamage(item, getAttackDamageForLevel(newLevel));
        }
    }

    public ItemStack create(int level) {
        ItemStack customSword = new ItemStack(Material.NETHERITE_SWORD, 1);
        ItemMeta meta = customSword.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§7« §fNormal Sword §7»");

            // Set item level
            meta.getPersistentDataContainer().set(itemLevelKey, PersistentDataType.INTEGER, level);

            // Set item durability
            int durability = 200;
            int maxDurability = getMaxDurabilityForLevel(level);
            meta.getPersistentDataContainer().set(durabilityKey, PersistentDataType.INTEGER, durability);
            meta.getPersistentDataContainer().set(maxDurabilityKey, PersistentDataType.INTEGER, maxDurability);
            meta.setUnbreakable(true);
            meta.setCustomModelData(3);

            // Set the lore for the item
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§7§m      §7« §bStats §7»§m      ");
            lore.add(" ");
            lore.add("§7Damage: §a" + getAttackDamageForLevel(level)); // Zeigt den Attack Damage in der Lore an
            lore.add("§7Durability: §a" + durability + " §7/ §a" + maxDurability);
            lore.add(" ");
            lore.add("§7§m      §7« §dReqir §7»§7§m      ");
            lore.add(" ");
            lore.add("§7Level: §a" + level);
            lore.add("§7Rarity: " + "§aOrdinary");
            lore.add(" ");
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            customSword.setItemMeta(meta);
            setSwordAttackDamage(customSword, getAttackDamageForLevel(level));
        }

        return customSword;
    }

    private int getMaxDurabilityForLevel(int level) {
        int baseDurability = 200;
        int increasePerLevel = 20;
        return baseDurability + (level - 1) * increasePerLevel;
    }

    public int getMaxDurabilityOfSword(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            int level = meta.getPersistentDataContainer().getOrDefault(itemLevelKey, PersistentDataType.INTEGER, 0);
            return getMaxDurabilityForLevel(level);
        }
        return 0;
    }

    public void updateSwordLore(ItemStack item, int level) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            int durability = meta.getPersistentDataContainer().getOrDefault(durabilityKey, PersistentDataType.INTEGER, 0);
            int maxDurability = getMaxDurabilityForLevel(level);

            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§7§m      §7« §bStats §7»§m      ");
            lore.add(" ");
            lore.add("§7Damage: §a" + getAttackDamageForLevel(level));
            lore.add("§7Durability: §a" + durability + " §7/ §a" + maxDurability);
            lore.add(" ");
            lore.add("§7§m      §7« §dReqir §7»§7§m      ");
            lore.add(" ");
            lore.add("§7Level: §a" + level);
            lore.add("§7Rarity: " + "§aOrdinary");
            lore.add(" ");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();

                if (meta != null && meta.getPersistentDataContainer().has(durabilityKey, PersistentDataType.INTEGER)) {
                    if (!player.hasMetadata("DURABILITY_REDUCED")) {
                        int durability = meta.getPersistentDataContainer().get(durabilityKey, PersistentDataType.INTEGER);

                        durability -= 1;
                        meta.getPersistentDataContainer().set(durabilityKey, PersistentDataType.INTEGER, durability);

                        // Lesen Sie den maxDurability Wert aus der PersistentDataContainer
                        int maxDurability = getMaxDurabilityOfSword(item);

                        // Aktualisiere die Lore
                        List<String> lore = meta.getLore();
                        for (int i = 0; i < lore.size(); i++) {
                            if (lore.get(i).startsWith("§7Durability: §a")) {
                                lore.set(i, "§7Durability: §a" + durability + " §7/ §a" + maxDurability);
                                break;
                            }
                        }
                        meta.setLore(lore);
                        item.setItemMeta(meta);

                        if (durability <= 0) {
                            player.getInventory().removeItem(item);
                        }

                        player.setMetadata("DURABILITY_REDUCED", new FixedMetadataValue(plugin, true));

                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> player.removeMetadata("DURABILITY_REDUCED", plugin), 1L);
                    }
                }
            }
        }
    }


    public static ItemStack getCustomSword(int level) {
        return new CustomSword(JavaPlugin.getProvidingPlugin(CustomSword.class)).create(level);
    }
}
