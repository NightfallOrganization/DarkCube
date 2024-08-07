/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.event;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EventInteract extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancel = false;
    private ItemStack item;
    private Inventory inventory;
    private ClickType click;
    private boolean isInteract;
    private WBUser user;

    public EventInteract(Player who, ItemStack item, Inventory inv, ClickType click) {
        this(who, item, inv, click, false);
    }

    public EventInteract(Player who, ItemStack item, Inventory inv, ClickType click, boolean isInteract) {
        super(who);
        user = WBUser.getUser(who);
        this.isInteract = isInteract;
        this.inventory = inv;
        this.item = item;
        this.click = click;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isInteract() {
        return isInteract;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ClickType getClick() {
        return click;
    }

    public WBUser getUser() {
        return user;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
