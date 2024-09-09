/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.perk.user;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.item.Item;

public class CooldownUserPerk extends DefaultUserPerk {
    private final Item cooldownItem;

    public CooldownUserPerk(WBUser owner, int id, int perkSlot, Perk perk, Item cooldownItem, Game game) {
        this(owner, perk, id, perkSlot, game, cooldownItem);
    }

    public CooldownUserPerk(WBUser owner, Perk perk, int id, int perkSlot, Game game, Item cooldownItem) {
        super(owner, perk, id, perkSlot, game);
        this.cooldownItem = cooldownItem;
    }

    protected boolean useCooldownItem() {
        return this.cooldown() > 0;
    }

    @Override
    protected Item currentItem() {
        return useCooldownItem() ? cooldownItem : super.currentItem();
    }
}
