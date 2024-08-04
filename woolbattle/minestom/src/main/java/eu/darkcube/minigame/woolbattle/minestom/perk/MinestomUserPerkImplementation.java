package eu.darkcube.minigame.woolbattle.minestom.perk;

import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;

public class MinestomUserPerkImplementation implements DefaultUserPerk.Implementation {
    @Override
    public void slotChanged(DefaultUserPerk userPerk, int oldSlot) {
        System.out.println("SetSlot");
    }
}
