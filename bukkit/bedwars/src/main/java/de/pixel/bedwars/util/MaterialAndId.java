package de.pixel.bedwars.util;

import org.bukkit.Material;

public class MaterialAndId {

	private Material material;
	private byte id;

	public MaterialAndId(Material material) {
		this(material, (byte) 0);
	}

	public MaterialAndId(Material material, byte id) {
		this.material = material;
		this.id = id;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	@Override
	public String toString() {
		return material.name() + ":" + id;
	}

	public void setId(byte id) {
		this.id = id;
	}

	public Material getMaterial() {
		return material;
	}

	public byte getId() {
		return id;
	}

	public static MaterialAndId fromString(String mat) {
		Material material = Material.valueOf(mat.split(":", 2)[0]);
		if (material == null) {
			return null;
		}
		try {
			byte id = mat.split(":", 2).length == 2 ? Byte.parseByte(mat.split(":", 2)[1]) : 0;
			return new MaterialAndId(material, id);
		} catch (Exception ex) {
			return null;
		}
	}
}
