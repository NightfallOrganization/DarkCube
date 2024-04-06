/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.npc;

import eu.darkcube.system.lobbysystem.Lobby;

public class FisherNPC {

    public static NPCManagement.NPC create() {
        NPCManagement.NPC npc = Lobby.getInstance().npcManagement().builder().profileHelper().name("§aFisher").skin(new FisherSkin()).builder().build();
        npc.location(Lobby.getInstance().getDataManager().getFisherNPCLocation());

        new NPCKnockbackThread(npc).runTaskTimer(Lobby.getInstance(), 5, 5);

        return npc;
    }

    static class FisherSkin extends NPCManagement.Skin {

        public FisherSkin() {
            super("ewogICJ0aW1lc3RhbXAiIDogMTY5Mjk3NzQwOTUzMCwKICAicHJvZmlsZUlkIiA6ICIwZDZjODU0ODA3ZGQ0NWZkYmMxZDEyMzY2OGY1ZWQwZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJXcWxmZnhJcmt0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y0YTAxZDcwZTYxOTQ1ZjRjZDVlNWI1ZWI5YzFhY2EzZWUyZjFkNDA2MTkyNjAyZGQ4MzFjNmI3MjYwM2I3NzEiCiAgICB9CiAgfQp9", "ApV80eCy2ZHF5ERW/ySy3PgE4H2eLz/iELAh6HS91mt27hfAkvtfCJgq4LVjNn91x/c55WaW+Z87039KY7Da17dWEQWJDxz7ZPZun1o1n8cj8khr9hhHk+E+YVTXvhyu/YQ1d3/+s5Rao1Wm0ld1zFhcS564N7p8InkIGuiEFJoFyWSEU1+64TYQCG2plJW2hmuS1K0dKLDY/XIdBhRZL6EtzhCdH2agvDTrH81TPZd2hKUD4pevl7jK4t6uOLQUpBJ10KV+T/8rm1+rI64wgkW/Wgowv9e32XDnXGMxg0bI3qvqmt+kgeD7GZ6Zkb4hEAw0Zkt4UCqUuS8bRp1oJHy1yhbiqz/ALzAh+8jTxoK+XVKynSbebayE+J7c/CfWXuibj6zpJX15Dlu1WhXEQt/pb4gfuzE8U5L3i7nqHnGiq+fKWMHXgbExDMdaE/QW5+xVpPEtBYNXn+5IUunLACSpigBOVAqK4IgINb2byMyT8JhIgV2c/xjDBLtzo4+n75uCrO4Du8wnLLVsyFUJE0mfh5UEBXboVg1w+MvZ6MFd43XHPKQpDcdPDkuR6w42PgiMYHmNJKeeVvXtsFHNtcMM4rqnVoClTsNLii6pVMAuEGAWcpnbk9dLpIApGmfi5/f8piBzV0xRdoj6P2UWVhS2HrRxlYiY0zMa8E8dgMg=");
        }

    }

}
