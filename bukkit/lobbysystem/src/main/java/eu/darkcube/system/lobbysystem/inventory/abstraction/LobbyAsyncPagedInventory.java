/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.inventory.abstraction;

import eu.darkcube.system.bukkit.inventoryapi.v1.DefaultAsyncPagedInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.bukkit.inventoryapi.v1.PageArrow;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.userapi.User;

import java.util.Collection;

public abstract class LobbyAsyncPagedInventory extends DefaultAsyncPagedInventory {

    protected final LobbyUser user;

    private boolean done = false;

    public LobbyAsyncPagedInventory(InventoryType inventoryType, Component title, LobbyUser user) {
        super(inventoryType, title, () -> !user.isAnimations());
        this.user = user;
        this.complete();
    }

    public LobbyAsyncPagedInventory(InventoryType inventoryType, Component title, User user) {
        this(inventoryType, title, UserWrapper.fromUser(user));
    }

    public LobbyAsyncPagedInventory(InventoryType inventoryType, Component title, int size, int[] box, LobbyUser user) {
        super(inventoryType, title, size, box, () -> !user.isAnimations());
        this.user = user;
        this.complete();
    }

    public LobbyAsyncPagedInventory(InventoryType inventoryType, Component title, int size, int[] box, User user) {
        this(inventoryType, title, size, box, UserWrapper.fromUser(user));
    }

    protected void playSound0() {
        super.playSound();
    }

    protected void complete() {
        this.done = true;
        this.insertDefaultItems();
        this.offerAnimations(this.informations);
    }

    public LobbyUser getUser() {
        return this.user;
    }

    protected boolean done() {
        return this.done;
    }

    @Override protected final void insertDefaultItems() {
        if (this.done()) {
            this.insertDefaultItems0();
        }
    }

    @Override protected final void playSound() {
        if (user.isSounds()) {
            playSound0();
        }
    }

    @Override protected void insertArrowItems() {
        this.arrowItem.put(PageArrow.NEXT, Item.ARROW_NEXT.getItem(user.user()));
        this.arrowItem.put(PageArrow.PREVIOUS, Item.ARROW_PREVIOUS.getItem(user.user()));
        super.insertArrowItems();
    }

    protected void insertDefaultItems0() {
        super.insertDefaultItems();
    }

    protected void asyncOfferAnimations0(Collection<AnimationInformation> informations) {
        super.asyncOfferAnimations(informations);
    }

    protected void offerAnimations0(Collection<AnimationInformation> informations) {
        super.offerAnimations(informations);
    }

    @Override protected final void asyncOfferAnimations(Collection<AnimationInformation> information) {
        if (this.done()) {
            this.asyncOfferAnimations0(information);
        }
    }

    @Override protected final void offerAnimations(Collection<AnimationInformation> informations) {
        if (this.done()) {
            this.offerAnimations0(informations);
        }
    }

}
