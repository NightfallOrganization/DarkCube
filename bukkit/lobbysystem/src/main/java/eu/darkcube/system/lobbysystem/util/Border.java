package eu.darkcube.system.lobbysystem.util;

import java.lang.reflect.Type;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import eu.darkcube.system.lobbysystem.parser.Locations;

public class Border implements Serializable {

	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Location.class, new JsonSerializer<Location>() {

				@Override
				public JsonElement serialize(Location loc, Type type, JsonSerializationContext con) {
					return new JsonPrimitive(Locations.serialize(loc));
				}

			})
			.registerTypeAdapter(Location.class, new JsonDeserializer<Location>() {

				@Override
				public Location deserialize(JsonElement json, Type type, JsonDeserializationContext con)
						throws JsonParseException {
					return Locations.deserialize(json.getAsString(), null);
				}

			})
			.create();

	private Shape shape;

	private double radius;

	private Location loc;

	private Location loc2;

	public Border(Shape shape, double radius, Location loc, Location loc2) {
		this.shape = shape;
		this.radius = radius;
		this.loc = loc;
		this.loc2 = loc2;
	}

	public boolean isInside(Entity ent) {
		boolean inside = false;
		if (this.loc.getWorld() != ent.getWorld())
			return true;
		Location o = ent.getLocation();
		switch (this.shape) {
		case CIRCLE:
			inside = this.loc.distance(o) < this.radius;
			break;
		case RECTANGLE:
			if (this.loc2 == null)
				return true;
			double sx = Math.min(this.loc.getX(), this.loc2.getX());
			double sy = Math.min(this.loc.getY(), this.loc2.getY());
			double sz = Math.min(this.loc.getZ(), this.loc2.getZ());
			double bx = Math.max(this.loc.getX(), this.loc2.getX());
			double by = Math.max(this.loc.getY(), this.loc2.getY());
			double bz = Math.max(this.loc.getZ(), this.loc2.getZ());
			double x = o.getX();
			double y = o.getY();
			double z = o.getZ();
//			return (sx < o.getX() && o.getX() > bx) && (sy < o.getY() && o.getY() > by)
//					&& (sz < o.getZ() && o.getZ() > bz);
			inside = sx < x && bx > x && sz < z && bz > z && sy < y && by > y;
			break;
		}
		return inside;
	}

	public Location getLoc1() {
		return this.loc.clone();
	}

	public Location getLoc2() {
		return this.loc2 == null ? null : this.loc2.clone();
	}

	public double getRadius() {
		return this.radius;
	}

	public Shape getShape() {
		return this.shape;
	}

	public boolean isOutside(Entity ent) {
		return !this.isInside(ent);
	}

	@Override
	public String serialize() {
		return Border.GSON.toJson(this);
	}

	public void boost(Entity ent) {
		Location mid = this.getMid();
		double x = mid.getX() - ent.getLocation().getX();
//		double y = ent.getLocation().getY() > Math.max(this.loc.getY(), this.loc2.getY()) ? mid.getY() - ent.getLocation().getY()
//				: 2;
		double ymax;
		switch (this.shape) {
		case CIRCLE:
			ymax = this.loc.getY() + this.radius;
			break;
		case RECTANGLE:
			ymax = Math.max(this.loc.getY(), this.loc2.getY());
			break;
		default:
			throw new UnsupportedOperationException();
		}
		double y = ent.getLocation().getY() > ymax ? mid.getY() - ent.getLocation().getY() : 2;
		double z = mid.getZ() - ent.getLocation().getZ();
		ent.setVelocity(new Vector(x, y, z).normalize().multiply(2));
	}

	public Location getMid() {
		switch (this.shape) {
		case CIRCLE:
			return this.loc;
		case RECTANGLE:
			return this.loc.clone().add(this.loc2).multiply(.5);
		}
		return this.loc;
	}

	public static enum Shape {
		RECTANGLE, CIRCLE
	}

}
