/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.PerkItem;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.server.item.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class ListenerInventoryClick extends Listener<InventoryClickEvent> {
    public static ListenerInventoryClick instance;
    private final WoolBattleBukkit woolbattle;

    public ListenerInventoryClick(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
        instance = this;
    }

    static int perkId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return -1;
        var b = ItemBuilder.item(item);
        if (!b.persistentDataStorage().has(PerkItem.KEY_PERK_ID)) return -1;
        return b.persistentDataStorage().get(PerkItem.KEY_PERK_ID, PerkItem.TYPE_PERK_ID);
    }

    @Override
    @EventHandler(priority = EventPriority.HIGH)
    public void handle(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (e.isCancelled()) return;

        var hotbarSlot = e.getHotbarButton();
        var slot = e.getRawSlot();

        var p = (Player) e.getWhoClicked();
        var user = WBUser.getUser(p);
        if (e.getView().getType() == InventoryType.CRAFTING) {
            if (slot == 0 || slot == 1 || slot == 2 || slot == 3 || slot == 4 || slot == -999 || slot == -1 || slot == 5 || slot == 6 || slot == 7 || slot == 8) {
                if (!user.isTrollMode()) {
                    e.setCancelled(true);
                }
                return;
            }
        } else if (!user.isTrollMode() && p.getGameMode() == GameMode.SURVIVAL) {
            e.setCancelled(true);
            return;
        } else {
            return;
        }

        var item = e.getCurrentItem();
        var cursor = e.getCursor();
        // Otherwise there is the possibility that I get the NORMAL slot 26 of the inventory and not
        // a hotbar-slot.
        var hotbar = hotbarSlot == -1 ? null : e.getView().getItem(hotbarSlot + 36);

        // boolean var1 = item != null && item.getType() != Material.AIR;
        // boolean var2 = cursor != null && cursor.getType() != Material.AIR;
        // boolean var3 = hotbar != null && hotbar.getType() != Material.AIR;
        var idItem = perkId(item);
        var idCursor = perkId(cursor);
        var idHotbar = perkId(hotbar);

        // String tagItem = var1 ? ItemManager.getItemId(item) : "Unknown Perk";
        // String tagCursor = var2 ? ItemManager.getItemId(cursor) : "Unknown Perk";
        // String tagHotbar = var3 ? ItemManager.getItemId(hotbar) : "Unknown Perk";

        var handles = new Handle[0];

        for (var perk : user.perks().perks()) {
            handles = Arrays.addAfter(handles, new Handle(perk));
        }
        if (idItem != -1 || idCursor != -1 || idHotbar != -1) {
            for (var handle : handles) {
                var id = handle.perk.id();
                // String tag = handle.getTag();
                switch (e.getAction()) {
                    case HOTBAR_SWAP:
                        if (id == idHotbar) {
                            handle.invoke(slot);
                        } else if (id == idItem) {
                            handle.invoke(hotbarSlot + 36);
                        } else if (id == idCursor) {
                            tue(idHotbar, idItem, idCursor, id);
                        }
                        break;
                    case CLONE_STACK:
                        if (id == idHotbar || id == idCursor) {
                            tue(idHotbar, idItem, idCursor, id);
                        }
                        break;
                    case COLLECT_TO_CURSOR:
                        if (id == idHotbar) {
                            tue(idHotbar, idItem, idCursor, id);
                        } else if (id == idCursor) {
                            woolbattle.sendConsole("Player " + p.getName() + " had slot error. Values:");
                            System.out.println("Slot: " + slot);
                            System.out.println("Tag: " + id);
                            System.out.println("HotbarButton: " + hotbarSlot);
                            System.out.println("Cursor: " + cursor);
                        }
                        break;
                    case DROP_ALL_CURSOR:
                    case DROP_ALL_SLOT:
                    case DROP_ONE_CURSOR:
                    case DROP_ONE_SLOT:
                        if (item != null && item.getType() == Material.WOOL) {
                            break;
                        }
                        if (item != null) {
                            if (item.getType() == Material.WOOL) {
                                break;
                            }
                            e.setCancelled(true);
                        }
                        break;
                    case MOVE_TO_OTHER_INVENTORY:
                        if (item != null) {
                            if (item.getType() == Material.WOOL) {
                                break;
                            }
                            e.setCancelled(true);
                        }
                        break;
                    case HOTBAR_MOVE_AND_READD:
                        e.setCancelled(true);
                        break;
                    case NOTHING:
                        break;
                    case PICKUP_ALL:
                        if (id == idItem) {
                            handle.invoke(100);
                        } else if (id == idHotbar || id == idCursor) {
                            tue(idHotbar, idItem, idCursor, id);
                        }
                        break;
                    case PICKUP_HALF:
                        if (id == idItem) {
                            e.setCancelled(true);
                        } else if (id == idHotbar || id == idCursor) {
                            tue(idHotbar, idItem, idCursor, id);
                        }
                        break;
                    case PICKUP_ONE:
                        if (id == idItem) {
                            e.setCancelled(true);
                        } else if (id == idHotbar || id == idCursor) {
                            tue(idHotbar, idItem, idCursor, id);
                        }
                        break;
                    case PICKUP_SOME:
                        if (id == idItem) {
                            e.setCancelled(true);
                        } else if (id == idHotbar || id == idCursor) {
                            tue(idHotbar, idItem, idCursor, id);
                        }
                        break;
                    case PLACE_ALL:
                        if (id == idCursor) {
                            handle.invoke(slot);
                        } else if (id == idItem) {
                            e.setCancelled(true);
                        } else if (id == idHotbar) {
                            tue(idHotbar, idItem, idCursor, id);
                        }
                        break;
                    case PLACE_ONE:
                        if (id == idCursor) {
                            new Scheduler(woolbattle) {
                                @Override
                                public void run() {
                                    handle.invoke(slot);
                                    handle.update();
                                    e.getView().setCursor(null);
                                }
                            }.runTask();
                        } else if (id == idHotbar || id == idItem) {
                            tue(idHotbar, idItem, idCursor, id);
                        }
                        break;
                    case PLACE_SOME:
                        if (id == idCursor) {
                            e.setCancelled(true);
                        } else if (id == idHotbar || id == idItem) {
                            tue(idHotbar, idItem, idCursor, id);
                        }
                        break;
                    case SWAP_WITH_CURSOR:
                        if (id == idItem) {
                            handle.invoke(100);
                        } else if (id == idCursor) {
                            handle.invoke(slot);
                        } else if (id == idHotbar) {
                            throw new UnknownError("Can not access this code");
                        }
                        break;
                    case UNKNOWN:
                        tue(idHotbar, idItem, idCursor, id);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + e.getAction());
                }
            }
        }
    }

    @EventHandler
    public void handle(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) {
            return;
        }
        var cursor = e.getView().getCursor();
        if (cursor == null || cursor.getType() == Material.AIR) {
            return;
        }
        var perkId = perkId(cursor);
        if (perkId == -1) return;
        var perk = WBUser.getUser((Player) e.getPlayer()).perks().perk(perkId);
        var user = perk.owner();
        var handles = new Handle[0];
        for (var p : user.perks().perks()) {
            handles = Arrays.addAfter(handles, new Handle(p));
        }

        for (var handle : handles) {
            if (handle.perk.id() == perkId) {
                var inv = user.getBukkitEntity().getInventory();
                var first = inv.firstEmpty();
                if (-first == 1) {
                    first = inv.first(Material.WOOL);
                }
                e.getView().setCursor(null);
                inv.setItem(first, cursor);
                handle.invoke(first < 9 ? first + 36 : first);
            }
        }
    }

    private void tue(int tagHotbar, int tagItem, int tagCursor, int tag) {
        throw new UnknownError("Can not access this code: hotbar: " + tagHotbar + ", item: " + tagItem + ", cursor: " + tagCursor + ", perkTag: " + tag);
    }

    static class Handle {

        private UserPerk perk;

        Handle(UserPerk perk) {
            this.perk = perk;
        }

        public void update() {
            if (perk != null) {
                perk.currentPerkItem().setItem();
            }
        }

        public UserPerk perk() {
            return perk;
        }

        public void invoke(int slot) {
            // System.out.println("new sot: " + slot);
            perk.slotSilent(slot);
        }
    }
}
