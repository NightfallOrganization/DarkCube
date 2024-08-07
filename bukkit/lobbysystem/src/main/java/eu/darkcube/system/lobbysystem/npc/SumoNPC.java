/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.npc;

import eu.darkcube.system.lobbysystem.Lobby;

public class SumoNPC {

    public static NPCManagement.NPC create() {
        NPCManagement.NPC npc = Lobby.getInstance().npcManagement().builder().profileHelper().name("§fSumo §8❂").skin(new SumoSkin()).builder().build();
        npc.location(Lobby.getInstance().getDataManager().getSumoNPCLocation());

        new NPCKnockbackThread(npc).runTaskTimer(Lobby.getInstance(), 5, 5);
        return npc;
    }

    public static class SumoSkin extends NPCManagement.Skin {

        public SumoSkin() {
            super("ewogICJ0aW1lc3RhbXAiIDogMTcxMjcwNjIzNzYxNywKICAicHJvZmlsZUlkIiA6ICI4ZjE5NjJmYzE4NzY0MDU3ODYxMmIxMzNjODE4YmY5OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOaW9uXzkiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmJkY2QzYjVjMjUzMzc4NDFhMGU4OGM0OTQzNWYyYzg0NjhiN2I2M2ZkNmZiNWQzYmRlYzU1YzMyNmM4MzI5ZiIKICAgIH0KICB9Cn0=", "yiV4JkpsMLbhpzTyjUlTVDlfxoZiXJUzQZDc3UMOUu27gEWTEy4wuTngZttPlOGYwTs8tKcnhF8fEvuUPsWLsdJ/wtDi6YOQQFzVKgb0Jq6E3thKIu34txpzyfMkMojywp2sZWYk8KgY8HmPX9CuHOHZgn8xuuyi4UsgUpNiFBlr8F2Ne3hHpJt0TtL7oQ3skLE6csFqc3NbpRzBcvbOu9Xr7y+DDx8dVE4bzj0C0nZq+FTeH1V/+ZVkPdi5y8dqr1tBX/g26wswieo1jut37SA9QyNLmCcOe9pWlTA3bYN7QxNazJqlgFRY302SW7JtvtPWnwiK4+qCIgrWvIqLdqv1RFWUrmlZ/QHe3g2jNFq3MF6H4JngKw/x7Iy0hEJ/WsDj6+U+O6FEqiafbl7c53LHKlxt9LO303DT1O3tijtc7K0U3PAD/ZrHkAYTKiwa/wbQjTXPRlT6C02wiwQj/lThnXM+TwdzUqwlGeTmlcw0w4gyvYNTrs7HUaUKC6DHtXo9yrgTj076jSu3zfUSunuJtd2D6NCSkMwxHy3DR4QkttC/LwMQE+m/tyP3KNE8PKNfkulMOEWo1CXJKgo7mCJy44WJZlqnOMDlhgE/0tB3UyndYZd1zjcsqpvhjgqjgyv8fV35TDmaKqq/fPq5/UnHD3qVn/ezSovTdvZKclQ=");
        }

    }

}
