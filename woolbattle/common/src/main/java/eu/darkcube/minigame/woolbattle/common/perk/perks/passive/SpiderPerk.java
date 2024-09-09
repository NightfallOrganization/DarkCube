package eu.darkcube.minigame.woolbattle.common.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class SpiderPerk extends Perk {
    public static final PerkName SPIDER = new PerkName("SPIDER");

    public SpiderPerk(CommonGame game) {
        super(ActivationType.PASSIVE, SPIDER, new Cooldown(Cooldown.Unit.TICKS, 60), 2, Items.PERK_SPIDER, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_SPIDER_COOLDOWN, g));
    }
}
