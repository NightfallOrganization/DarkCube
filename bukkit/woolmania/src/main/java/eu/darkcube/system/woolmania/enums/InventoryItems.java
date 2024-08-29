/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums;

import static eu.darkcube.system.server.item.ItemBuilder.item;
import static eu.darkcube.system.server.item.component.ItemComponent.ATTRIBUTE_MODIFIERS;
import static eu.darkcube.system.util.Language.lastStyle;
import static org.bukkit.Material.*;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.TextComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.item.ItemFactory;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.ItemComponent;
import eu.darkcube.system.server.item.component.components.AttributeList;
import eu.darkcube.system.server.item.component.components.HeadProfile;
import eu.darkcube.system.server.item.component.components.util.PlayerSkin;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataTypes;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.hall.Halls;
import eu.darkcube.system.woolmania.util.message.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public enum InventoryItems implements ItemFactory {
    // Shop
    INVENTORY_SHOP_FOOD(item(COOKED_BEEF)),
    INVENTORY_SHOP_GADGETS(item(FISHING_ROD)),
    INVENTORY_SHOP_SOUNDS(item(MUSIC_DISC_PIGSTEP).hideJukeboxPlayableTooltip()),

    // Shop-Food
    INVENTORY_SHOP_FOOD_CARROT(item(ItemBuilder.item(Material.CARROT)), 1000),
    INVENTORY_SHOP_FOOD_MELON(item(ItemBuilder.item(MELON)), 5000),
    INVENTORY_SHOP_FOOD_STEAK(item(ItemBuilder.item(COOKED_BEEF)), 10000),
    INVENTORY_SHOP_FOOD_DIAMOND(item(ItemBuilder.item(Material.DIAMOND)), 20000),

    // Shop-Gadgets
    INVENTORY_SHOP_GADGETS_GRENADE(item(SNOWBALL), 5000),

    // Shop-Sound
    INVENTORY_SHOP_SOUNDS_STANDARD(item(MUSIC_DISC_WARD).hideJukeboxPlayableTooltip(), 0),
    INVENTORY_SHOP_SOUNDS_WOOLBATTLE(item(MUSIC_DISC_MALL).hideJukeboxPlayableTooltip(), 1500000),
    INVENTORY_SHOP_SOUNDS_ARMADILLO(item(MUSIC_DISC_13).hideJukeboxPlayableTooltip(), 15000),
    INVENTORY_SHOP_SOUNDS_ARMADILLO_HIGH(item(MUSIC_DISC_13).hideJukeboxPlayableTooltip(), 15000),
    INVENTORY_SHOP_SOUNDS_SCAFFOLDING(item(MUSIC_DISC_CREATOR_MUSIC_BOX).hideJukeboxPlayableTooltip(), 500000),
    INVENTORY_SHOP_SOUNDS_SCAFFOLDING_HIGH(item(MUSIC_DISC_CREATOR_MUSIC_BOX).hideJukeboxPlayableTooltip(), 50000),

    // Teleport
    INVENTORY_TELEPORT_SPAWN(item(NETHER_STAR)),
    INVENTORY_TELEPORT_HALLS(item(BARREL)),
    INVENTORY_TELEPORT_NONE(item(FIREWORK_STAR)),

    // Teleport-Halls
    INVENTORY_TELEPORT_HALLS_HALL_1(item(WHITE_SHULKER_BOX), Halls.HALL1),
    INVENTORY_TELEPORT_HALLS_HALL_2(item(ORANGE_SHULKER_BOX), Halls.HALL2),
    INVENTORY_TELEPORT_HALLS_HALL_3(item(MAGENTA_SHULKER_BOX), Halls.HALL3),
    // INVENTORY_TELEPORT_HALLS_HALL_4(item(MAGENTA_SHULKER_BOX), Halls.HALL4),
    // INVENTORY_TELEPORT_HALLS_HALL_5(item(MAGENTA_SHULKER_BOX), Halls.HALL3),
    // INVENTORY_TELEPORT_HALLS_HALL_6(item(MAGENTA_SHULKER_BOX), Halls.HALL3),
    // INVENTORY_TELEPORT_HALLS_HALL_7(item(MAGENTA_SHULKER_BOX), Halls.HALL3),
    // INVENTORY_TELEPORT_HALLS_HALL_8(item(MAGENTA_SHULKER_BOX), Halls.HALL3),
    // INVENTORY_TELEPORT_HALLS_HALL_9(item(MAGENTA_SHULKER_BOX), Halls.HALL3),


    // Smith
    INVENTORY_SMITH_UPGRADE(item(GRINDSTONE)),
    INVENTORY_SMITH_REPAIR(item(ANVIL)),
    INVENTORY_SMITH_SHOP(item(CAULDRON)),

    //<editor-fold desc="Smith-Repair">
    INVENTORY_SMITH_REPAIR_25(item(PLAYER_HEAD).set(ItemComponent.PROFILE, new HeadProfile(new PlayerSkin("ewogICJ0aW1lc3RhbXAiIDogMTcyNDM1OTY3MzE4NCwKICAicHJvZmlsZUlkIiA6ICI2YzdjZWFjNTdjM2Q0ZTdmYWI3MTc3YTRkMDhlMGU2MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNRzE0MSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lODQ3Y2ZlMjk3N2U3OTE1MDRiMjJjYzJmZTA1OGUyOTc3OTRmYjFmMWI3ODllYTIzMDA4ODQ0M2RmYzdkODFmIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
            "WoX1fz4qJhXu/zQbN/gFQvdAdkW/bc9ZRO8J0JKdTFMyumenW/tyNMIgjoSnj5duMtKlMqJP/LW0e+uu1gRweQnFlVf4NQm6zeelg3weQg0LY+41IO/kTotBK9LBE32bKgOBZZAqyxsYXu21rUFHvqfwbZLT0QGr/q+YTMSHCT36S5IZ5LjraRXmnIeRU6boSA3fYAZQ6p+O62qBf+Ez3wLABwNaiADp+Jw3llss4SnnFYz1H7FzVZsXbEVX/g5EI5etj36WDMX/wfWrvZFwgnveKsIaKJLlVA2tBbuarJ691VbPNqlGSsYaaRo7n8cKhUw3IkLrrZm8RKGA8UPWjrH9KXTT/Vk6oBkMsByLLFq1yof0KFbQ0QZxE6SqgTid5b59XXsZ91RL/D/doTgiHq5i5l6zUoyUlQj51HYwQrJ+kQR+gpa0vcYtEewgvFNyx3+YVZb1UaI5DX78E2nSqiPTV5lKyNN0iSiuRisYEfJ5VPQ5p4uXiu8l6hK+wRkddg8rmw4lpqkUIlDvAS3l+6+nNI22okwYvavxu0vK65CLeubxhNnhq0XFktcrPTXK0guDbIZI844yx3EZ1GlOeR/zV+9WGD8oOW8+93U/315w1nbnsls0FKgP+Ebh6EPz7Um7EZr+6tbnIjGIYgL7e8qPvWV6h4d+yQl8I+AJsWY=")))),
    INVENTORY_SMITH_REPAIR_50(item(PLAYER_HEAD).set(ItemComponent.PROFILE, new HeadProfile(new PlayerSkin("ewogICJ0aW1lc3RhbXAiIDogMTcyNDM1OTcyNzcwNywKICAicHJvZmlsZUlkIiA6ICIyMmNiM2U0ZDdmOTY0ZmNjYjE1MDIyNWIwN2YzMTEyMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJ0dWxleW1hbkIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Q2ZjQ4ZjQzMmE0ZWYzMmM4NjdmNzgwNmYxOTQzNDZlMTlkMWMwYTAwNDg5Nzc2MWNkNTg4NjY3MWIyN2YwYyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9", "KbQUtifqTtw9w5FlUcdpFzK7Vy88Tx1tZmG90KOWvn1sCAO64tZ59LZestRjpUYvEh1hOOdM2NfwXLPKjsU+HGutRrsUrNRGsNL9aBF6IY6To5/OutAnoy/vsQCGJLYq7nf3sH8jK3b9Jo9gtESc4koijv/DNjVLaA1uH1zQedaZVzmUHw/hvsJAvb8tnVbYLmK09DXkzvaHDn/+OtVthpJdse/gYBGa4UqSTWbADhq4DsDzI8u1AesbnPOi1ercvlBlqu4JxeaWoG2E9yb9nuzTP4IqzZEiMXUu369+diwL0O3UNk3p84PYpDY0KA7QWSmOB902doaDHX8USJG+fXjCH7R2K/DTg7L3/jmjkWsSWClzsE9v/TVESoOKY/+L1BSqwaZQzIgf9eQRkFW32AtHD0DmkTmNsmRMS/dY75ZvrlipSf4BzCgLiK0/ZeI9+eek23nzlfTsyPeypHcVY2nboJm19Z34jXAlLCd6KAy4n6P17gzIlya5JSkUY5ssfIIat3a5twDhQfYmBXW9s8tuSk/6rKUIORMTlo6fSnLHW0FX9N5Fkp7VyzEhm+VHUOncOOA/miqPusyQSlYrAi3XLnSWP8J9YCSgtrGXRONRtA3a4W1q+E9hUixj7f/fCUDHFJjm31M9mb+bDmY8sAcjdlMR74TaWo7kyagb114=")))),
    INVENTORY_SMITH_REPAIR_75(item(PLAYER_HEAD).set(ItemComponent.PROFILE, new HeadProfile(new PlayerSkin("ewogICJ0aW1lc3RhbXAiIDogMTcyNDM1OTg1MTg2NywKICAicHJvZmlsZUlkIiA6ICI4ZjllYTBhNWJhOGE0NTNkYTgzNTBmYjRmNzVmOTJiOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJDeWJlck1pbm55IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzc2N2M4MWE2MDgwYTA1YWZjMTFjMDYxNWU1YWM5MDNmZjYyZmE0MjE3YTBkYzE1MTUxMzg1YTQ4MTFiNjVkNzgiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "B0ADva1lYaKOeoIpkuGyC53Qv4rp0rfKmqLhGfSVX0FYkmFf2QayYRCMsIloAVejnVEHZrPAb9jiXmOeu3bslqWbu4YbBa+Ihc3V6u4lpczyiLGWaw2V/oxKZge4DZQUBdsJeNSn3Tb4kk3M9S1HiEYN6I4p3eBHmRIq8ke1B0AWYZD/80AIwsqlFrza2erUx5BKLioCjsQf1UjwRDEnqrQIav91Yn6aKNQq6wySJgXDUEY/E9diWh1O6KR7y0qXf/EfAaVA+M7D1vieW8g+hAx7neaJMT7e+k9Vcha7LN1l2qOiTPa6OTHcEFvLUB5smfsDDOIWEDNJrgq68pUXLluYLrk+Fc9KnmelbwMdR4YKBYZJ04jLRu5jWk61VaP2MDqmOWe8sUAvmp/vSuJzqi0etAh6k1vg0vjThVwHtOIqKDCVMDDldHSHN2FBLGp0DpjrhEGpXta2WVEym+Molpr03Davn8ZDdOt3Xj6P6JZANXPhSPIeEhqJsjWdty4YyhzgZwq2dFXQqdIcRMw4rd5D7TbNudREZ1GkH+xf6F5nl5S/mD+pJJGzHu4+lEJudWOZ3516XgNEu1zmgvACpOQit0dKGtd/AibmRdAFIJZkZxPai/4CzHEzDgYcit0vID2/q525y+Ech338EiY5v8lo1PXRgAQBghhc+2/Yseo=")))),
    INVENTORY_SMITH_REPAIR_100(item(PLAYER_HEAD).set(ItemComponent.PROFILE, new HeadProfile(new PlayerSkin("ewogICJ0aW1lc3RhbXAiIDogMTcyNDM1OTg4NTQ4OSwKICAicHJvZmlsZUlkIiA6ICI5MWYwNGZlOTBmMzY0M2I1OGYyMGUzMzc1Zjg2ZDM5ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTdG9ybVN0b3JteSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81MTJmYjhhMDU1ZGU1N2M2NDViZjY2YjA4MmYwYmMwOGVjNzQwMzRkYjJlNjRhNTM5ZTE1OWM0YjkzMmU1YTUxIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=", "UJE3a82X0Oxe8lDYSNHSrFBxye29I43f02MqhZp5UGKKBVtkWwUL0LPv5/oKuZMCFfemVD1Rufn8DhC/RzQLMUYHMOtO13vXvLzJQOACVNFc0Z/UDgMPg5GhykXZO6xO0tV2EoeEOfkphAnuNVqrjSSIJVr6PPwpUTYQSy6hgKdIp8ZuUxkI1GAAaAaYdqE6edoLNQ3tfTKbDHpp6zpNbFgEFXDpoqWcZ7WCVcDo/EZ4WEyqriqUh0BUscT0MPibvuqNzxi2fLZuoSYVNA9zOgX+5NvpATk9xd7WbAvelYNCybY08J4udCMZJCcPqio1uEZ0FvDQ+cKejvu4iMYCDc9M3bQueTmo8fwsTLFHgN5bAd6xQtwPz1A4z0qGeybIeXQG17yOlE4fnE4Gr5LZEUZUN1HXDCTlLLVxTEAXVlz+ov/6i3EOtiFkHrsVZXode7eyi7OQceiyz9tDiSjh5sFo7ypo4xg6aEgfJmfSPzX47JgZTLKGJmLCrV+as3I11+iIK4a9V91SwgijPsFOstOm29kGuQLRsp4VPywdOdkVa7zGFTvAoyk/MjHSTcCZr7MuaoAX5wFOuFCsVMf4JP7GWdFhzWloIwzDmPGhfwVKF14Iew0pfxtxsgZcE9uUefmiSv4/J1O67omqZ26EJdk9POJGioyqcJKPjEh++Hs=")))),

    INVENTORY_SMITH_REPAIR_25_NONE(item(PLAYER_HEAD).set(ItemComponent.PROFILE, new HeadProfile(new PlayerSkin("ewogICJ0aW1lc3RhbXAiIDogMTcyNDM3MTI2MzczMSwKICAicHJvZmlsZUlkIiA6ICJmNzM0MmExODMxZDA0ZDA5ODc4Y2ViOTVmOTUxYTllMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb3RNMWtzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzNmNDEzZmRmM2Q2ODc5MWZlZDUzN2NlMGJkNjZiMDViZWEzZTJkMzdhN2RiNzdjYzhmZTIxOWU1NWI2ZDQzMDIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
            "n/429BzJCMd9mIxGm+JskhhfdL8D2IHJ9nKcmYWlxuopLmu/ay0VxVONOFXkQOiuG+tm4ki3ZVvgHD1XCwqR0R+aZvIki+MT8K/Sk7CVOv1Uxh3tP6drv9mR0hI4lo2acx434HVfCY4lu1qoQiDEjueA93y/STU0NdklUiHF6CqfjzWaQgzgYprvwXX4oZAlgW3z8bFtVSeB1susjk30azquRR251e8cHiQa/ubxpO/UyhGKTSDjt/GPKPvxvifkN3Cl5cNWYZrxmXkvvfj9a76Fqtcofdd1i6CPzCxGpITHCcoWZuWiE4FLTDNJKQXhyXaIlNT3y9Iv7ROapYcVcouc3v4HdpnpR1i5EWMQPfvp0nJA7T/lGmjIHiIGjR7G/bF81ESSO7sXzpaulID+MYPkRzv7e2xImiN+Y+h1vpVI+tNPg8VjsSmmpIku9tW0w0UntWEDqpaPyHeHVyIEX+udokZLdof3EkWUl+FflCYLUdAgXNkf+EtO7cJumskzPcqs7LuirpG6eH/sXR6+wCElMRchlbh6w2/ni3p+PlgD9hEJurlU3vPhuLgsiq6z7yHUoWhhEOo0btq/P27L4FtoTBagJ5MYk0R812nm0EbJXkTRt/h1AAXTA2s/VetGIm1JxKclG6IgzH7fjcB6oYON/fclnn5FzIA7pmEsiG0=")))),
    INVENTORY_SMITH_REPAIR_50_NONE(item(PLAYER_HEAD).set(ItemComponent.PROFILE, new HeadProfile(new PlayerSkin("ewogICJ0aW1lc3RhbXAiIDogMTcyNDM3MTMxMDY2NCwKICAicHJvZmlsZUlkIiA6ICIwNDg2YWUwMWI4Y2I0OWUzODMyZDcwOTNmMWJlNzI3NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJfcGFrbWFuXyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zYzI3MWY4MTZkNTJiZWIwZDU4ZWM2NmI0YzQ1YWIxMzkxODhmMDdlYjRjYjIxYTlhYTAxZTM1MzNhZmY1MDY2IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
            "fcqpMxxoHED6zeW+GXIw5XRJkaSehs6IK/xUCiUsTw74kTxkbb0U1mXS7nI94DyUSP/JQVYgf12009huNQnPBq+mreqNIi60zEXRsHLOjDHwCevWQZeFgClqsEcuxiSB3TjrIqA9KUNLBW/P80Uz0uAG5zfji9dWmGvEfH+VKbXH+um5SOdqzegR2rMm+2CWLMsDMaDHLMHfaLvarHrnH9/ba9O476SV4BsSStgxXZiq8vOpF/2JwVAxivcWw14OKAIdGS3QxSGkxUCjmF9JOsTct+ZnK5TkTVX6f2SxubY16zB8sydYD1SgnAW02MoXStTOf7DDFDUFFNLsSMj9GElKBx5t1g7qbCfIV38eRgfcz7sBQkdNwve+URPwSOIDU/3czkyJ44b3kD0saesaVYzVnMb5oIyJiFtMx3vgF1hslFP4HM7wZRovPOURZzd/eaxhJVKxOkc4EgX0O/DVa3OSFewM59BuJpZ3pMHld7DZGsvmMuI254MHSrRGklwwZi5j7Z8CR896gZ/BE9TZDsRRwyVRyvGsjFIGGE8iVfXfou0fJYt2yA+QtX3L2nixUe8NQu7tVoIHEiqt125oq0Qj1pAPUf5rHdWM/rGDJWBjRIHfXnntxto1JoZrF1Eel6C0NxFAX/vNjLphNXUH7rGNGcugdfY1DUdmCBWoOH8=")))),
    INVENTORY_SMITH_REPAIR_75_NONE(item(PLAYER_HEAD).set(ItemComponent.PROFILE, new HeadProfile(new PlayerSkin("ewogICJ0aW1lc3RhbXAiIDogMTcyNDM3MTM2NjM0MiwKICAicHJvZmlsZUlkIiA6ICIxMDJiMWQxOGFhZTg0ZjQzYWMyYTY1MjQyNzdjMDU0NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNeXRob2xvdGwiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTRmZmU4MzllZjVhYmM2ODI5YTQ4OTZkZjA1YzMyYzkwOTNkNTIwZjk1ZTk4MTQxNjRhOWY0OWI0MGI4YTk4OCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
            "IiVtlFWKMqS0xTTswSCuc4fDt/7kkqdRqXilTKvQ2t/FCY/CBfWfGn4+D/OoJ6s4KI14TrdGJ6TRDbGLzsgiFBbNNs1NuL4ONFl7/Y70adCgug1vVQ6MXCmYTSmSl7rC+Gj+jHF51biKA+1lx1B3iYSBFweIWHmX3rjXQSTn/dbICfRjvJ35FdsRhpgoTxHca0D4Hz43mSisG3AEkRO/v0GUou7S1lXMzNJupADiDEo6pNDlcrEc/paN7PG3b2XGWBuO6VaCIkVpwqKghW5JDvj2oYXbPtLMIOiOgdEe//F3JuURvMqvQAIeXJ4CKFcEcnIoHW4vBdtdOagdHdYWAknAuLD7lfbIiQZ6OwOuVzg/r9NH/aYUshuXi0WIFQ5OgaKyfWX/Cu9RODdJrqsmUsOAKsjezFm5HgDSqAW88Emh2128GQxrLkhBbeCRqWrtlS2H2AJVAfNmEmkWgamesUFoBabg3SFjlQhHRbqoZzXMLuMuR49ucrsBScKZ1OQkV7jiZFmLzBlTNynNHi1vc+kg8XTVI5Qx7HdpacEHMb1ayPNEzlvrhQxwnTs3ni4O7M+isNXuYjEm0vcBWXjgmnVCdeT2FrwfmfEeInuI0D8t5m1RHt0fw4bbwQkiChKNoazvpNrhv0Btfklp9/8BOcHnbjwb4DzaEiBx+kWed4Y=")))),
    INVENTORY_SMITH_REPAIR_100_NONE(item(PLAYER_HEAD).set(ItemComponent.PROFILE, new HeadProfile(new PlayerSkin("ewogICJ0aW1lc3RhbXAiIDogMTcyNDM3MTM5Nzc3MywKICAicHJvZmlsZUlkIiA6ICIzMzk0ZmVlMmIwMTQ0N2E0YmRhNDdlY2IyMzQ3YzI0ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJQaFg4MDEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM2ZGY3MjVmNmQzOGU5OGNmMWUxNGYzZWRkM2VmNTFmM2NlZGRlZjFiY2RjNDI4MTM2MWE2ZjAzNTY4ODBhYSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
            "xkaexOuHk5Xs75iax5FQAyqRd0gYaXI+uKiXVtv51XQJfK+Ih7WzAhRjXoeG8zJ4sHtnGbhCDT1wq2kvs/UbQ1uFJg+TK5R2ooGFL7IXYe1O4I3sE3w99UVt6Gq0nnirJDbcOIM43W3rhIvdl0w5Zhq/jku3QYTGFwZc7DdihPt5NUQeyD9RdMdvGMBcKYg37joHxR17oFo5Vg+gUBJTV+BGWg0VZpYM6EXoDDUrRH7aVNRpcqKw/UufZ4I/7lI6lvVbDR2ooS+Y+qDTLYFII/JTZDKHGmUOvLabYZWG23/v4CCg+Ox4gk2PIM9IgN7/lCi6wMU9n/QFvjwXUgCA5oBsljpoTPgkvdvje4L2XVbb6ZbTQIo2mMjE4k9NoKqLNfeDycy9PjnCrNJb9sRXc4UxHHxDNTzpfpzF8u3MJuDOaK8Z/bjq7hgtrCVmyEuTL1FWDHW4WAQOBDwVWt/QyNdbUE4I3XV1E+rcgvoK84i1UBrSZf4AW72ur2hEly16nsv6xx3DzA4VSkTb6bGrn6XwNnF9MlqRkA3MNAvJATZfZ9eIM957wURW+BTGoCglVeiUAhPS9+IIPG9Uy0zbRjku0hl1eS65tDj2XErxt3y07WuzwYhWB0AD5O7DYS0oF42sP1rs/nFT02R0yenBnWmam0tk52H5HGD8dzU9jRQ=")))),

    INVENTORY_SMITH_REPAIR_AIR(item(AIR)),
    //</editor-fold>

    // Smith-Shop
    INVENTORY_SMITH_SHOP_ITEMS(item(CROSSBOW)),

    // Smith-Shop-Items
    INVENTORY_SMITH_SHOP_ITEMS_SHEARS(item(SHEARS), 0),

    // Ability
    INVENTORY_ABILITY_ITEM_AUTO_EAT(item(RABBIT_STEW)),
    INVENTORY_ABILITY_ITEM_FLY(item(FEATHER)),
    INVENTORY_ABILITY_ITEM_SPEED(item(DIAMOND_BOOTS).set(ATTRIBUTE_MODIFIERS, AttributeList.EMPTY.withTooltip(false))),

    // INVENTORY_ABILITY_OWN(item(HEAVY_CORE)),
    INVENTORY_ABILITY_OWN(item(HEAVY_CORE)),
    INVENTORY_ABILITY_SHOP(item(OMINOUS_BOTTLE).hideAdditionalTooltip()),

    ;

    public static final DataKey<String> ITEM_ID = DataKey.of(Key.key(WoolMania.getInstance(), "item_id"), PersistentDataTypes.STRING);
    private final String key;
    private final ItemBuilder builder;
    private Integer cost;
    private Halls hall;

    InventoryItems(ItemBuilder builder, Halls hall) {
        this.builder = builder;
        this.hall = hall;
        key = this.name();
    }

    InventoryItems(ItemBuilder builder, Integer cost) {
        this.builder = builder;
        this.cost = cost;
        key = this.name();
    }

    InventoryItems(ItemBuilder builder) {
        this.builder = builder;
        key = this.name();
    }

    public Integer getCost() {
        return cost;
    }

    public @NotNull String key() {
        return key;
    }

    public Halls getHall() {
        return hall;
    }

    public String itemID() {
        return Message.PREFIX_INVENTORY_ITEM + key();
    }

    public @NotNull ItemBuilder builder() {
        return builder.clone();
    }

    public ItemBuilder createItem(Player player) {
        return createItem(UserAPI.instance().user(player.getUniqueId()));
    }

    @Override
    public @NotNull ItemBuilder createItem(@NotNull User user) {
        return getItem(user);
    }

    public @NotNull ItemBuilder getItem(@NotNull User user) {
        return getItem(user, true);
    }

    public @NotNull ItemBuilder getItem(@NotNull User user, Object... displayNameReplacements) {
        return getItem(user, displayNameReplacements, true);
    }

    public @NotNull ItemBuilder getItem(@NotNull User user, boolean storeItemId) {
        return getItem(user, new Object[0], storeItemId);
    }

    public @NotNull ItemBuilder getItem(@NotNull User user, Object[] displayNameReplacements, boolean storeItemId) {
        ItemBuilder builder = this.builder();
        if (storeItemId) {
            builder.persistentDataStorage().set(ITEM_ID, itemID());
        }
        Language language = user.language();
        Component name;
        try {
            name = Message.getMessage(this.itemID(), language, displayNameReplacements);
        } catch (Throwable t) {
            System.out.println(this.itemID());
            t.printStackTrace();
            throw t;
        }
        builder.displayname(name);

        if (language.containsMessage(Message.KEY_PREFIX + Message.PREFIX_INVENTORY_ITEM + Message.PREFIX_LORE + key())) {
            Component lore = Message.getMessage(Message.PREFIX_INVENTORY_ITEM + Message.PREFIX_LORE + key(), language);

            String serialized = LegacyComponentSerializer.legacySection().serialize(lore);
            String[] loreStringLines = serialized.split("\n");
            Style lastStyle = null;
            for (String loreStringLine : loreStringLines) {
                TextComponent loreLine = LegacyComponentSerializer.legacySection().deserialize(loreStringLine);
                if (lastStyle != null) {
                    Style newStyle = loreLine.style().merge(lastStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
                    loreLine = loreLine.style(newStyle);
                }

                lastStyle = lastStyle(loreLine);
                builder.lore(loreLine);
            }
        }
        return builder;
    }

    @Nullable
    public static String getItemID(ItemBuilder item) {
        return item.persistentDataStorage().get(ITEM_ID);
    }
}
