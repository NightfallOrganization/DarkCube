package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ListenerSlimeJump extends PerkListener{

    //todo add lore for the items and perk
    PerkType perkType = PerkType.SLIMEJUMP;
    @EventHandler
    public void handle(PlayerInteractEvent e){
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {


            User user = Main.getInstance().getUserWrapper().getUser(e.getPlayer().getUniqueId());
            if (super.checkUsable(user, Item.PERK_SLIMEJUMP, e.getItem() )){

                new Scheduler(){

                }.runTaskLater(15);
            }
        }
    }



}
