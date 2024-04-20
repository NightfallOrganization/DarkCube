package eu.darkcube.minigame.woolbattle.common.util;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class GsonUtil {
    private static final Gson gson = new GsonBuilder().create();

    public static @NotNull Gson gson() {
        return gson;
    }
}
