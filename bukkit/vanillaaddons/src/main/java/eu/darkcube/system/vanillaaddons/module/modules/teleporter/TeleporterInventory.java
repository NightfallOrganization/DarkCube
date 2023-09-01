/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules.teleporter;

import eu.darkcube.system.bukkit.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.JoinConfiguration;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import eu.darkcube.system.vanillaaddons.AUser;
import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.inventory.AbstractInventory;
import eu.darkcube.system.vanillaaddons.inventory.AddonsAsyncPagedInventory;
import eu.darkcube.system.vanillaaddons.module.modules.teleporter.Teleporter.TeleportAccess;
import eu.darkcube.system.vanillaaddons.module.modules.teleporter.TeleporterModule.TeleporterListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class TeleporterInventory extends AbstractInventory<AddonsAsyncPagedInventory, Teleporter> {
    public static final InventoryType TYPE = InventoryType.of("teleporter");

    @Override protected AddonsAsyncPagedInventory openInventory(AUser user) {
        Teleporter teleporter = data();
        final Key KEY_TYPE = new Key(user.addons(), "teleporter_inventory_type");
        AddonsAsyncPagedInventory i = new AddonsAsyncPagedInventory(TYPE, Component
                .text("\uDAFF\uDFEFⲌ")
                .color(NamedTextColor.WHITE), () -> true) {

            @Override protected void inventoryClick(IInventoryClickEvent event) {
                if (Objects.equals(event.bukkitEvent().getClickedInventory(), event.bukkitEvent().getView().getTopInventory())) {
                    event.setCancelled(true);
                }
                ItemStack bitem = event.bukkitEvent().getView().getItem(event.bukkitEvent().getRawSlot());
                ItemBuilder item = bitem == null ? null : ItemBuilder.item(bitem);
                if (item == null) return;
                if (!item.persistentDataStorage().has(KEY_TYPE)) return;
                int type = item.persistentDataStorage().get(KEY_TYPE, PersistentDataTypes.INTEGER);
                if (type == 0) {
                    //noinspection DataFlowIssue
                    if (event.bukkitEvent().getCursor() == null || event.bukkitEvent().getCursor().getItemMeta() == null) return;
                    if (data().owner() == null) data().owner(user.user().uniqueId());
                    if (!user.user().uniqueId().equals(data().owner())) return;
                    //noinspection DataFlowIssue
                    data().icon(event.bukkitEvent().getCursor());
                    TeleporterListener.saveTeleporters(VanillaAddons.instance(), data().block().block().getWorld());
                    insertFallbackItems();
                    updateSlots.add(IInventory.slot(3, 4));
                    recalculate();
                } else if (type == 1) {
                    if (data().owner() == null) data().owner(user.user().uniqueId());
                    if (!user.user().uniqueId().equals(data().owner())) return;
                    user.openInventory(TeleporterRenameInventory.TYPE, data());
                } else if (type == 2) {
                    data().access(TeleportAccess.values()[(data().access().ordinal() + 1) % TeleportAccess.values().length]);
                    insertFallbackItems();
                    updateSlots.add(IInventory.slot(3, 6));
                    recalculate();
                } else if (type == 3) {
                    user.openInventory(TeleporterTrustedListInventory.TYPE, data());
                }
            }

            @Override protected void insertFallbackItems() {
                ItemStack icon = ItemBuilder
                        .item(teleporter.icon())
                        .displayname(Component.text("Icon").color(TextColor.color(120, 120, 120)))
                        .lore(Component.translatable(teleporter.icon().translationKey()))
                        .flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_UNBREAKABLE)
                        .persistentDataStorage()
                        .iset(KEY_TYPE, PersistentDataTypes.INTEGER, 0)
                        .builder()
                        .build();
                ItemStack name = ItemBuilder
                        .item(Material.NAME_TAG)
                        .displayname(Component.text("Name").color(TextColor.color(120, 120, 120)))
                        .lore(Component.join(JoinConfiguration.separator(Component.space()), Component
                                .text("Name:")
                                .color(TextColor.color(120, 120, 120)), data().dname()))
                        .persistentDataStorage()
                        .iset(KEY_TYPE, PersistentDataTypes.INTEGER, 1)
                        .builder()
                        .build();
                ItemStack access = ItemBuilder
                        .item(teleporter.access().getType())
                        .displayname(Component.join(JoinConfiguration.separator(Component.space()), Component
                                .text("Zugriff:")
                                .color(TextColor.color(120, 120, 120)), Component
                                .text(teleporter.access().name().toLowerCase())
                                .color(TextColor.color(170, 0, 170))))
                        .persistentDataStorage()
                        .iset(KEY_TYPE, PersistentDataTypes.INTEGER, 2)
                        .builder()
                        .build();
                ItemStack trustedList = ItemBuilder
                        .item(Material.POPPY)
                        .displayname(Component.text("Vertrauenswürdige Spieler").color(TextColor.color(120, 120, 120)))
                        .persistentDataStorage()
                        .iset(KEY_TYPE, PersistentDataTypes.INTEGER, 3)
                        .builder()
                        .build();
                if (teleporter.owner() == null) teleporter.owner(user.user().uniqueId());
                if (user.user().uniqueId().equals(teleporter.owner())) {
                    fallbackItems.put(IInventory.slot(3, 2), name);
                    fallbackItems.put(IInventory.slot(3, 4), icon);
                    fallbackItems.put(IInventory.slot(3, 6), access);
                    fallbackItems.put(IInventory.slot(3, 8), trustedList);
                } else {
                    fallbackItems.put(IInventory.slot(3, 4), name);
                    fallbackItems.put(IInventory.slot(3, 6), icon);
                }
//				super.insertFallbackItems();
            }
        };
        Player p = Bukkit.getPlayer(user.user().uniqueId());
        i.open(p);
        return i;
    }

    @Override protected void closeInventory(AUser user, AddonsAsyncPagedInventory inventory) {
    }
}
