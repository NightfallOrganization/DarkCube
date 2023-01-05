package eu.darkcube.system.miners.listener;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.gamephase.miningphase.Miningphase;
import eu.darkcube.system.miners.player.Message;
import eu.darkcube.system.miners.player.TNTManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class ListenerPlayerDamage implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        if (Miners.getGamephase() == 0 || Miners.getGamephase() == 3) // can't take damage in lobby or end
            e.setCancelled(true);

        if (e.getFinalDamage() >= ((Player) e.getEntity()).getHealth()) { // if this hit would kill the player handle the death
            e.setDamage(0);
            handleDeath(e);
        }
    }

    @EventHandler
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;

        if (Miners.getTeamManager().isOwnerInSameTeam((Player) e.getEntity(), e.getDamager())) {
            e.setDamage(0);
            return;
        }

        if (e.getFinalDamage() < ((Player) e.getEntity()).getHealth())
            return;

        if (e.getDamager() instanceof Player) // send appropriate death message
            Miners.sendTranslatedMessageAll(Message.PLAYER_WAS_KILLED, e.getEntity().getCustomName(), e.getDamager().getCustomName());
        else if (e.getDamager() instanceof TNTPrimed)
            handleTntHitDeath(e);
        else if (e.getDamager() instanceof Arrow)
            handleArrowHitDeath(e);
        else // death by entity that is neither a Player, Arrow nor TNT = "Player died" death message
            Miners.sendTranslatedMessageAll(Message.PLAYER_DIED, e.getEntity().getCustomName());
    }

    private void handleDeath(EntityDamageEvent e) {
        if (Miners.getGamephase() == 2) {
            Miners.getTeamManager().setPlayerTeam((Player) e.getEntity(), 0, true);
            dropInventory((Player) e.getEntity());
        }
        respawnPlayer((Player) e.getEntity());

        if (e instanceof EntityDamageByEntityEvent) // if it's a DamageByEntity, the listener will handle it
            return;

        Miners.sendTranslatedMessageAll(Message.PLAYER_DIED, e.getEntity().getCustomName());
    }

    private void handleArrowHitDeath(EntityDamageByEntityEvent e) {
        Arrow a = (Arrow) e.getDamager();
        if (a.getShooter() instanceof Player) // player owned arrow = "Player was killed by Owner"
            Miners.sendTranslatedMessageAll(Message.PLAYER_WAS_KILLED, e.getEntity().getCustomName(), ((Player) a.getShooter()).getCustomName());
        else // non-player owned arrow = "Player died"
            Miners.sendTranslatedMessageAll(Message.PLAYER_DIED, e.getEntity().getCustomName());
    }

    private void handleTntHitDeath(EntityDamageByEntityEvent e) {
        TNTPrimed t = (TNTPrimed) e.getDamager();
        if (TNTManager.getTNTOwner(t) != null) // player owned tnt = "Player was killed by Owner"
            Miners.sendTranslatedMessageAll(Message.PLAYER_WAS_KILLED, e.getEntity().getCustomName(), TNTManager.getTNTOwner(t).getCustomName());
        else // non-player owned tnt = "Player died"
            Miners.sendTranslatedMessageAll(Message.PLAYER_DIED, e.getEntity().getCustomName());
    }

    private void respawnPlayer(Player p) {
        p.setHealth(p.getMaxHealth());
        p.getActivePotionEffects().forEach(pot -> p.removePotionEffect(pot.getType()));
        p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 0, false, false));
        p.setVelocity(new Vector(0, 0, 0));
        if (Miners.getGamephase() == 1) {
            p.teleport(Miningphase.getSpawn(Miners.getTeamManager().getPlayerTeam(p)));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 4, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5, 0, false, false));
            p.playSound(p.getLocation(), Sound.GHAST_CHARGE, 1, 1);
        }
    }


    private void dropInventory(Player p) {
        Location loc = p.getLocation();
        for (ItemStack item : p.getInventory().getContents()) {
            if (!(item.getType().equals(Material.WORKBENCH) || item.getType().equals(Material.COBBLESTONE))) { // don't drop certain items
                Item dropItem = loc.getWorld().dropItem(loc, item);
                dropItem.setVelocity(new Vector(Math.random(), Math.random(), Math.random())); // apply a random velocity to the item
            }
        }
    }

}
