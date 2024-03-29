package eu.darkcube.minigame.woolbattle.common.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class AdventureUtil {
    private static final MethodHandle toJson;
    private static final MethodHandle fromJson;
    private static final Object gson;

    public static String serialize(Style style) {
        try {
            return (String) toJson.invoke(gson, style);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Style deserialize(String serialized) {
        try {
            return (Style) fromJson.invoke(gson, serialized, Style.class);
        } catch (Throwable e) {
            try {
                return Style.style(NamedTextColor.NAMES.value(new Gson().fromJson(serialized, String.class)));
            } catch (Throwable ignored) {
            }
            e.printStackTrace();
            return Style.empty();
        }
    }

    static {
        try {
            var lookup = MethodHandles.publicLookup();
            var method = GsonComponentSerializer.class.getMethod("serializer");
            var gsonClass = method.getReturnType();
            var serializer = lookup.findVirtual(GsonComponentSerializer.class, "serializer", MethodType.methodType(gsonClass));
            gson = serializer.invoke(GsonComponentSerializer.gson());
            toJson = lookup.findVirtual(gsonClass, "toJson", MethodType.methodType(String.class, Object.class));
            fromJson = lookup.findVirtual(gsonClass, "fromJson", MethodType.methodType(Object.class, String.class, Class.class));
        } catch (Throwable t) {
            throw new Error(t);
        }
    }
}
