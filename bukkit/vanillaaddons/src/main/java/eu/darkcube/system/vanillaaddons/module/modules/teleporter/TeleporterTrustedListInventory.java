/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.vanillaaddons.module.modules.teleporter;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import eu.darkcube.system.bukkit.inventoryapi.v1.IInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.meta.SkullBuilderMeta;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import eu.darkcube.system.vanillaaddons.AUser;
import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.inventory.AbstractInventory;
import eu.darkcube.system.vanillaaddons.inventory.AddonsAsyncPagedInventory;
import eu.darkcube.system.vanillaaddons.module.modules.teleporter.TeleporterModule.TeleporterListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class TeleporterTrustedListInventory extends AbstractInventory<AddonsAsyncPagedInventory, Teleporter> {
    public static final InventoryType TYPE = InventoryType.of("teleporter_trusted_list");

    @Override protected AddonsAsyncPagedInventory openInventory(AUser user) {
        final Key KEY_TYPE = new Key(VanillaAddons.instance(), "type");
        return new AddonsAsyncPagedInventory(TYPE, Component.text("\uDAFF\uDFEFḅ").color(NamedTextColor.WHITE), () -> true) {
            {
                open(Bukkit.getPlayer(user.user().uniqueId()));
            }

            @Override protected void inventoryClick(IInventoryClickEvent event) {
                ItemBuilder item = event.item();
                if (Objects.equals(event.bukkitEvent().getClickedInventory(), event.bukkitEvent().getView().getTopInventory()))
                    event.setCancelled(true);
                if (item == null) return;
                if (!item.persistentDataStorage().has(KEY_TYPE)) return;
                int type = item.persistentDataStorage().get(KEY_TYPE, PersistentDataTypes.INTEGER);
                if (type == 0) {
                    AUser.user(event.user()).openInventory(TeleporterTrustedListAddInventory.TYPE, data());
                } else if (type == 1) {
                    if (event.bukkitEvent().getClick() != ClickType.RIGHT) return;
                    UUID target = item.meta(SkullBuilderMeta.class).owningPlayer().uniqueId();
                    data().trustedList().remove(target);
                    TeleporterListener.saveTeleporters(VanillaAddons.instance(), data().block().block().getWorld());
                    recalculate();
                }
            }

            @Override protected void fillItems(Map<Integer, ItemStack> items) {
                int id = 0;
                for (UUID uuid : data().trustedList()) {
                    OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);// 374
                    String name = op.hasPlayedBefore() ? op.getName() == null ? "Unbekannter Spieler" : op.getName() : "Unbekannter Spieler";
                    items.put(id++, ItemBuilder
                            .item(Material.PLAYER_HEAD)
                            .meta(new SkullBuilderMeta().owningPlayer(new SkullBuilderMeta.UserProfile(name, uuid)))
                            .displayname(Component.text(name).color(TextColor.color(255, 0, 0)))
                            .lore(Component
                                    .text("Rechtsklick um Spieler Zugriff zu entfernen")
                                    .color(TextColor.color(120, 120, 120)), Component
                                    .text(uuid.toString())
                                    .color(TextColor.color(80, 80, 80)))
                            .persistentDataStorage()
                            .iset(KEY_TYPE, PersistentDataTypes.INTEGER, 1)
                            .builder()
                            .build());
                }
            }

            @Override protected void insertFallbackItems() {
                fallbackItems.put(IInventory.slot(1, 5), ItemBuilder
                        .item(Material.EMERALD)
                        .displayname(Component.text("Spieler hinzufügen").color(TextColor.color(170, 0, 170)))
                        .persistentDataStorage()
                        .iset(KEY_TYPE, PersistentDataTypes.INTEGER, 0)
                        .builder()
                        .build());
            }
        };
    }

    @Override protected void closeInventory(AUser user, AddonsAsyncPagedInventory inventory) {
    }
}
