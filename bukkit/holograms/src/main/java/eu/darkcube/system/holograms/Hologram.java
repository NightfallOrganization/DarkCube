package eu.darkcube.system.holograms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

public class Hologram {

	private List<String> text = new ArrayList<>();
	private List<ArmorStand> armorStands = new ArrayList<>();
	private Location position;

	public Hologram() {
	}

	public void addText(String... lines) {
		this.text.addAll(Arrays.asList(lines));
		recreate();
	}

	public Hologram position(Location position) {
		this.position = position;
		return this;
	}

	public Hologram create() {
		double lineOffset = 0.3;
		double offset = (text.size() - 2) * lineOffset;
		for (String line : text) {
			Location loc = position.clone();
			loc.setY(loc.getY() + offset);
			ArmorStand stand = loc.getWorld().spawn(loc, ArmorStand.class);
			stand.setGravity(false);
			stand.setMarker(true);
			stand.setVisible(false);
			stand.setCustomName(line);
			stand.setCustomNameVisible(true);
			offset -= lineOffset;
			this.armorStands.add(stand);
		}
		return this;
	}

	public void recreate() {
		remove();
		create();
	}

	public void remove() {
		armorStands.forEach(Entity::remove);
		armorStands.clear();
	}

}
