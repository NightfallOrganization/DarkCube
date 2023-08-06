//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.darkcube.system.citybuild.commands;

import java.util.Iterator;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;

public class NoEffectPlugin implements Listener {
    public NoEffectPlugin() {
    }// 17

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LivingEntity && event.getEntity() instanceof Player) {// 21
            LivingEntity monster = (LivingEntity)event.getDamager();// 22
            Player player = (Player)event.getEntity();// 23
            Iterator var4 = monster.getActivePotionEffects().iterator();

            while(var4.hasNext()) {
                PotionEffect effect = (PotionEffect)var4.next();// 25
                player.removePotionEffect(effect.getType());// 26
            }
        }

    }// 29
}
