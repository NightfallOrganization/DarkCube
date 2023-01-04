package eu.darkcube.system.miners.gamephase.pvpphase;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.util.Timer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

public class PVPPhase {

    public static World PVP_WORLD;

    private final Spawns spawns;
    private WorldBorder worldBorder;
    private Timer pvpTimer;

    public PVPPhase() {
        PVP_WORLD = Bukkit.getWorld(Miners.PVP_WORLD_NAME);
        spawns = new Spawns(PVP_WORLD);
        worldBorder = PVP_WORLD.getWorldBorder();
        worldBorder.setCenter(PVP_WORLD.getSpawnLocation());
        worldBorder.setSize(200);
        worldBorder.setDamageBuffer(0);
        worldBorder.setDamageAmount(1);
        worldBorder.setWarningDistance(0);
        worldBorder.setWarningTime(10);

        PVP_WORLD.setGameRuleValue("naturalRegeneration", "true");
        PVP_WORLD.setSpawnLocation(0, 100, 0);

        pvpTimer = new Timer() {
            @Override
            public void onIncrement() {
            }

            @Override
            public void onEnd() {
//				Bukkit.getOnlinePlayers().forEach(p -> {
//					if (Miners.getTeamManager().getPlayerTeam(p) != 0)
//						p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100000, 2, true, true), true);
//				});
            }

        };
        pvpTimer.setNotificationInterval(60);
        pvpTimer.addCustomNotification(1, 2, 3, 4, 5, 10, 30);
    }

    public void enable() {
        for (int i = 1; i <= Miners.getMinersConfig().TEAM_COUNT; i++) {
            Location l = spawns.getRandomSpawn();
            Miners.getTeamManager().getPlayersInTeam(i).forEach(p -> p.teleport(l));
        }
        Miners.getTeamManager().getPlayersWithoutTeam().forEach(p -> p.teleport(PVP_WORLD.getSpawnLocation()));
        worldBorder.setSize(0, Miners.getMinersConfig().PVP_PHASE_DURATION);

        pvpTimer.start(Miners.getMinersConfig().PVP_PHASE_DURATION);
    }

    public void disable() {
        pvpTimer.cancel(false);
    }

    public Timer getTimer() {
        return pvpTimer;
    }

}
