/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.commands;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class KnockbackListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // Schaden ausstellen
        event.setCancelled(true);

        // Check ob Schädiger ein Spieler ist und ob das Ziel eine Entität ist
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Entity) {
            Player player = (Player) event.getDamager();
            Entity entity = event.getEntity();

            // AttackCooldown und Kritstatus bestimmen
            float attackCooldown = player.getAttackCooldown();
            boolean isCritical = player.getFallDistance() > 0 && !player.isOnGround() &&
                    !player.isInsideVehicle() && !player.hasPotionEffect(PotionEffectType.BLINDNESS) &&
                    player.getLocation().getPitch() < 50;

            // Knockback-Stärke berechnen
            double strength = getKnockbackStrength(player.getInventory().getItemInMainHand(), attackCooldown, isCritical);
            Vector direction = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();

            // Anpassung der Y-Komponente basierend auf dem Bodenstatus der Entität
            if (entity.isOnGround()) {
                direction.setY(0.7);  // Setze Y-Geschwindigkeit direkt
            } else {
                direction.setY(entity.getVelocity().getY() + 0.1);  // Addiere einen Wert zur aktuellen Y-Geschwindigkeit
            }

            direction.multiply(strength);

            // Knockback anwenden
            entity.setVelocity(direction);
        }
    }

    private double getKnockbackStrength(ItemStack item, float attackCooldown, boolean isCritical) {
        double baseStrength;
        if (item == null || item.getType() == Material.AIR) {
            baseStrength = 0.3;  // Grundwert für leere Hand
        } else {
            baseStrength = 0.3;  // Grundwert für eine Waffe
        }

        double minimumKnockback = 0.2;  // Mindest-Knockback-Wert

        double knockbackMultiplier = isCritical ? 1.5 : 1;  // Multiplikator für kritischen Treffer

        return (baseStrength + minimumKnockback) * attackCooldown * knockbackMultiplier;
    }

}
