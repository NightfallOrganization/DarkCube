package eu.darkcube.system.miners.gamephase.miningphase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.util.Timer;

public class Miningphase {

	private Timer miningTimer;

	private static final Random RANDOM = new Random();

	public static final int CUBE_DISTANCE = 100;
	public static final Vector SPAWN_OFFSET = new Vector(33.5, 32, 33.5);

	public static World MINING_WORLD;

	public Miningphase() {
		MINING_WORLD = Bukkit.getWorld(Miners.MINING_WORLD_NAME);
		
		for (Map<String, String> map : readVeinConfig()) {
			if (map.containsKey("type") && map.containsKey("count")) {
				generateVeinsFromMap(map);
			}
		}

		miningTimer = new Timer() {

			@Override
			public void onIncrement() {
			}

			@Override
			public void onEnd() {
				// startNextPhase();
				System.out.println("init gamephase 2");
			}
		};
		miningTimer.start(Miners.getMinersConfig().MINING_PHASE_DURATION * 1000);

	}

	/**
	 * returns the location of the north-western bottom corner of a given team's
	 * cube
	 */
	public static Location getCubeBase(int team) {
		return new Location(MINING_WORLD, ((team - 1) * CUBE_DISTANCE), 100, 0);
	}

	/**
	 * returns the spawnpoint of a given team in the mining world
	 */
	public static Location getSpawn(int team) {
		return getCubeBase(team).clone().add(SPAWN_OFFSET);
	}

	/**
	 * read json with ore vein generation from the config
	 */
	private static final List<Map<String, String>> readVeinConfig() {
		Gson gson = new Gson();
		String json = Miners.getMinersConfig().CONFIG.getString("generation.veins");
		List<String> list1 = gson.fromJson(json, new TypeToken<List<String>>() {
		}.getType());
		List<Map<String, String>> list2 = new ArrayList<>();
		for (String s : list1)
			list2.add(gson.fromJson(s, new TypeToken<Map<String, String>>() {
			}.getType()));
		return list2;
	}

	private static final void generateVeinsFromMap(Map<String, String> map) {
		try {
			Material material = Material.valueOf(map.get("type"));
			int count = Integer.parseInt(map.get("count"));
			int min = 1, max = 1;
			if (map.containsKey("minSize") && map.containsKey("maxSize")) {
				min = Integer.parseInt(map.get("minSize"));
				max = Integer.parseInt(map.get("maxSize"));
			}
			Miners.log("Generating " + count + " veins of type \"" + material + "\" with a size between " + min
					+ " and " + max);
			for (int i = 0; i <= count; i++)
				MiningGenerator.generateOreVein(MiningGenerator.pickRandomBlock(), material, randomBetween(min, max));

		} catch (Exception e) {
			Miners.log("Invalid ore vein generation config!");
			if (e instanceof NumberFormatException)
				Miners.log("Invalid number!");
			else
				Miners.log("Invalid block material!");
		}
	}

	private static int randomBetween(int min, int max) {
		if (min >= max)
			return min;
		int i = RANDOM.nextInt(max - min);
		return i + min;
	}

}
