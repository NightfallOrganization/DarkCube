package eu.darkcube.system.skyland.Listener;

import eu.darkcube.system.skyland.Skyland;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;

public class DamageListener implements Listener {

    Skyland skyland;

    public DamageListener(Skyland skyland){
        this.skyland = skyland;
    }

    public int calculateDmg(SkylandPlayer sk){

        //todo calc dmg
        return 0;
    }


    @EventHandler
    public void handleDmg(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player p = ((Player) e.getEntity());


            if(e.getEntity().getMetadata("spawnProt").isEmpty()){
                System.out.println("No meta found for: " + p.getUniqueId().toString());
            }else {
                if(p.getMetadata("spawnProt").get(0).asBoolean()){
                    e.setCancelled(true);
                }
            }


        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){

        e.getPlayer().setMetadata("spawnProt", new FixedMetadataValue(Skyland.getInstance(), true));//todo 9

        if(e.getPlayer().getMetadata("spawnProt").isEmpty()){
            e.getPlayer().sendMessage("no meta");
        }else {
            e.getPlayer().sendMessage(e.getPlayer().getMetadata("spawnProt").get(0).asString());
            e.getPlayer().sendMessage(e.getPlayer().getMetadata("spawnProt").get(1).asString());
        }

        if(e.getPlayer().getMetadata("skylandPlayer").isEmpty()){
            e.getPlayer().sendMessage("Welcome to Skyland!");

        }else {
            e.getPlayer().sendMessage("Welcome back to Skyland!");

        }
    }


}
