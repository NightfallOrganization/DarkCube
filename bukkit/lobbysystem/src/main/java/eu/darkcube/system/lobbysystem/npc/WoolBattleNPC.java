/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.npc;

import eu.darkcube.system.lobbysystem.Lobby;

public class WoolBattleNPC {

    public static NPCManagement.NPC create() {
        NPCManagement.NPC npc = Lobby.getInstance().npcManagement().builder().profileHelper().name("§5Wool§dBattle").skin(new WoolBattleSkin()).builder().build();
        npc.location(Lobby.getInstance().getDataManager().getWoolBattleNPCLocation());

        new NPCKnockbackThread(npc).runTaskTimer(Lobby.getInstance(), 5, 5);
        return npc;
    }

    public static class WoolBattleSkin extends NPCManagement.Skin {

        public WoolBattleSkin() {
            super("ewogICJ0aW1lc3RhbXAiIDogMTU5MTYyOTc4Mzc2MSwKICAicHJvZmlsZUlkIiA6ICJiMGM5MjQ2ZTcyMDE0ZDI2YTc3NWFmNDIyMTZkOTUwMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJFbGl0ZURhcmtuZXNzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzZkMTg3MzkxZmM2ZmNjYzhhMDdmMmI2NGJiZGUyODgwN2NjZTZjOGZiNzA0MDZiY2U2ZTcxNDg2MDc0YzdjOGMiCiAgICB9CiAgfQp9", "Bgywd/32NsH1kHtHRSz7pxeXTfaOPjFOzLjAFkV+Vjdq4gqCuoEMhkLo8wVXYk8tmUkz53+6VcQjoQhheJ0kjBXWVbLdGO83apAbjveZ4hOezDatW9Vv8YbD8+9vjEFOs8HuPWB1b6630xZKqcNwQqE+Q4q3hJBRxtlnV/qw8SjsJDKaTA8gLtJ/xl2fa5WNprjIvfULr7RUrJCSfB+a9BZKKcESnQ6ta7id+sGw/j0v/0eBBoq975SW0MSdDTuknn1P64qZTzJfEuIv1etTj2F+Wl6JVrEyamtPFKSA+rltbh64Foz4ebIV0JxIkpjCMO3puwsB2P5NqzvbRLjS7Jr0sEgORGpZD4Gck02Q2+cIQ9wuPkiKGn9sO5fGY97gXB4p/zfVOqty56lUclp7nnuw962bTUD8Mkl1jJQWoKlWOjLNVC9FY9XaTMyaReMH4Xgu6FVMPdEzyrGKndJPD6mkwzWCC+QQaeAK6bgaOMewrdct6NLi0NJch/lQSJ1F9mhZffV2dJrIFl4ul7ngxgw2JPxKNr8vAEFI00HlaLBEm38Zy0A4sj4H/7iYTU+/0i7QQio+/AKJWGcOQw8ZHvfCG1L8GvxYUTp15N/Nn9UNyiBeAvrKEnz6vVTHQ16qJgS0N8wBvYipKIxY6JF483VS+PMsaxtpAi5uhKi4X9s=");
        }

    }

}
