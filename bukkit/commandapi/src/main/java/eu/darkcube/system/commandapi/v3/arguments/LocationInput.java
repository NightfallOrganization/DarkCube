package eu.darkcube.system.commandapi.v3.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Vector2f;
import eu.darkcube.system.commandapi.v3.Vector3d;

public class LocationInput implements ILocationArgument {
	private final LocationPart x;
	private final LocationPart y;
	private final LocationPart z;

	public LocationInput(LocationPart x, LocationPart y, LocationPart z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public Vector3d getPosition(CommandSource source) {
		Vector3d vector3d = source.getPos();
		return new Vector3d(this.x.get(vector3d.x), this.y.get(vector3d.y), this.z.get(vector3d.z));
	}

	@Override
	public Vector2f getRotation(CommandSource source) {
		Vector2f vector2f = source.getRotation();
		return new Vector2f((float) this.x.get(vector2f.x), (float) this.y.get(vector2f.y));
	}

	@Override
	public boolean isXRelative() {
		return this.x.isRelative();
	}

	@Override
	public boolean isYRelative() {
		return this.y.isRelative();
	}

	@Override
	public boolean isZRelative() {
		return this.z.isRelative();
	}

	@Override
	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (!(p_equals_1_ instanceof LocationInput)) {
			return false;
		} else {
			LocationInput locationinput = (LocationInput) p_equals_1_;
			if (!this.x.equals(locationinput.x)) {
				return false;
			} else {
				return !this.y.equals(locationinput.y) ? false : this.z.equals(locationinput.z);
			}
		}
	}

	public static LocationInput parseInt(StringReader reader) throws CommandSyntaxException {
		int i = reader.getCursor();
		LocationPart locationpart = LocationPart.parseInt(reader);
		if (reader.canRead() && reader.peek() == ' ') {
			reader.skip();
			LocationPart locationpart1 = LocationPart.parseInt(reader);
			if (reader.canRead() && reader.peek() == ' ') {
				reader.skip();
				LocationPart locationpart2 = LocationPart.parseInt(reader);
				return new LocationInput(locationpart, locationpart1, locationpart2);
			} else {
				reader.setCursor(i);
				throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
			}
		} else {
			reader.setCursor(i);
			throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
		}
	}

	public static LocationInput parseDouble(StringReader reader, boolean centerIntegers) throws CommandSyntaxException {
		int i = reader.getCursor();
		LocationPart locationpart = LocationPart.parseDouble(reader, centerIntegers);
		if (reader.canRead() && reader.peek() == ' ') {
			reader.skip();
			LocationPart locationpart1 = LocationPart.parseDouble(reader, false);
			if (reader.canRead() && reader.peek() == ' ') {
				reader.skip();
				LocationPart locationpart2 = LocationPart.parseDouble(reader, centerIntegers);
				return new LocationInput(locationpart, locationpart1, locationpart2);
			} else {
				reader.setCursor(i);
				throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
			}
		} else {
			reader.setCursor(i);
			throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
		}
	}

	public static LocationInput current() {
		return new LocationInput(new LocationPart(true, 0.0D), new LocationPart(true, 0.0D),
				new LocationPart(true, 0.0D));
	}

	@Override
	public int hashCode() {
		int i = this.x.hashCode();
		i = 31 * i + this.y.hashCode();
		return 31 * i + this.z.hashCode();
	}
}
