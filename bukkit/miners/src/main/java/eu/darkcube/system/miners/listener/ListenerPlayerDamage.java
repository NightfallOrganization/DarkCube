package eu.darkcube.system.miners.listener;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.gamephase.miningphase.Miningphase;
import eu.darkcube.system.miners.player.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
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

        if (e.getFinalDamage() >= ((Player) e.getEntity()).getHealth()) // if this hit would kill the player handle the death
            handleDeath(e);
    }

    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) // if damaged entity isn't a player, return
            return;

        Entity entity = e.getDamager();
        if (entity instanceof Arrow && ((Arrow) entity).getShooter() instanceof Player) // if damager is an arrow shot by a player, get that player
            entity = (Player) ((Arrow) entity).getShooter();
        else if (!(entity instanceof Player))   // if damager is not a player, an arrow, or an arrow shot by a player, return
            return;

        if (Miners.getTeamManager().getPlayerTeam((Player) e.getEntity()) == Miners.getTeamManager().getPlayerTeam((Player) e.getDamager())) // can't damage your teammates
            e.setCancelled(true);

        if (e.getFinalDamage() >= ((Player) e.getEntity()).getHealth()) { // if this hit would kill the player handle the death
            e.setDamage(0);
            Miners.sendTranslatedMessageAll(Message.PLAYER_WAS_KILLED, e.getEntity().getCustomName(), entity.getCustomName());
        }
    }

    private void handleDeath(EntityDamageEvent e) {
        if (Miners.getGamephase() == 2) {
            Miners.getTeamManager().setPlayerTeam((Player) e.getEntity(), 0, true);
            dropInventory((Player) e.getEntity());
        }
        respawnPlayer((Player) e.getEntity());

        if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) ||
                e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE))
            return;

        e.setDamage(0);
        Miners.sendTranslatedMessageAll(Message.PLAYER_DIED, e.getEntity().getCustomName());
    }

    private void respawnPlayer(Player p) {
        p.setHealth(p.getMaxHealth());
        p.getActivePotionEffects().forEach(pot -> p.removePotionEffect(pot.getType()));
        p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 0, false, false));
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
