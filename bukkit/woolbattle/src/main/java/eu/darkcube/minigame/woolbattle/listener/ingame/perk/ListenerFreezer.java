package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerFreezer extends BasicPerkListener {


    public ListenerFreezer() {
        super(PerkType.FREEZER);
    }

    @Override
    protected boolean activateRight(User user, Perk perk) {
        //user.
        return true;

    }
}
