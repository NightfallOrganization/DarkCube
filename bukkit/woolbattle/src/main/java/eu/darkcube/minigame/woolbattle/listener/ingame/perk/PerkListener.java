package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class PerkListener implements Listener {

    //to add a new Perk you need to add perktype, lore for the perk, perk item, perk item lore, and thenn add the effect
    protected boolean checkUsable(User user, Player p) {
        Perk perk = user.getPerkByItemId(ItemManager.getItemId(p.getItemInHand()));
        if (!p.getInventory().contains(Material.WOOL, PerkType.CAPSULE.getCost()) || perk.getCooldown() > 0) {
            Ingame.playSoundNotEnoughWool(user);
            new Scheduler() {
                @Override
                public void run() {
                    perk.setItem();
                }
            }.runTask();
            return false;
        }

        if(perk.getItem().equals(p.getItemInHand())){
            return true;
        }
        return false;
    }

    protected void payForThePerk(Player p, User user, PerkType perkType) {
        ItemManager.removeItems(user, p.getInventory(),
                new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColor()),
                perkType.getCost());
    }


    protected void startCooldown(Perk perk) {

        //if error add a check for 0 cooldown

        new Scheduler() {
            int cd = perk.getMaxCooldown() + 1;

            @Override
            public void run() {
                if (cd <= 1) {
                    this.cancel();
                    perk.setCooldown(0);
                    return;
                }
                perk.setCooldown(--cd);
            }
        }.runTaskTimer(20);
    }
}
