/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

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

    public static MaterialAndId fromString(String mat) {
        Material material = Material.valueOf(mat.split(":", 2)[0]);
        try {
            byte id = mat.split(":", 2).length == 2 ? Byte.parseByte(mat.split(":", 2)[1]) : 0;
            return new MaterialAndId(material, id);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String toString() {
        return material.name() + ":" + id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }
}
