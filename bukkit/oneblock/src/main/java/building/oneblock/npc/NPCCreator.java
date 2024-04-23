package building.oneblock.npc;

import building.oneblock.manager.NPCManager;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import java.util.UUID;

import static building.oneblock.manager.WorldManager.SPAWN;

public class NPCCreator implements Listener {
    private NPCManager npcManager;

    public NPCCreator(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    public void createNPC() {
        npcManager.createNPC(new Location(SPAWN, 35.5, 99, -8.5, 0, 0), new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTU5MTYyOTc4Mzc2MSwKICAicHJvZmlsZUlkIiA6ICJiMGM5MjQ2ZTcyMDE0ZDI2YTc3NWFmNDIyMTZkOTUwMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJFbGl0ZURhcmtuZXNzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzZkMTg3MzkxZmM2ZmNjYzhhMDdmMmI2NGJiZGUyODgwN2NjZTZjOGZiNzA0MDZiY2U2ZTcxNDg2MDc0YzdjOGMiCiAgICB9CiAgfQp9", "Bgywd/32NsH1kHtHRSz7pxeXTfaOPjFOzLjAFkV+Vjdq4gqCuoEMhkLo8wVXYk8tmUkz53+6VcQjoQhheJ0kjBXWVbLdGO83apAbjveZ4hOezDatW9Vv8YbD8+9vjEFOs8HuPWB1b6630xZKqcNwQqE+Q4q3hJBRxtlnV/qw8SjsJDKaTA8gLtJ/xl2fa5WNprjIvfULr7RUrJCSfB+a9BZKKcESnQ6ta7id+sGw/j0v/0eBBoq975SW0MSdDTuknn1P64qZTzJfEuIv1etTj2F+Wl6JVrEyamtPFKSA+rltbh64Foz4ebIV0JxIkpjCMO3puwsB2P5NqzvbRLjS7Jr0sEgORGpZD4Gck02Q2+cIQ9wuPkiKGn9sO5fGY97gXB4p/zfVOqty56lUclp7nnuw962bTUD8Mkl1jJQWoKlWOjLNVC9FY9XaTMyaReMH4Xgu6FVMPdEzyrGKndJPD6mkwzWCC+QQaeAK6bgaOMewrdct6NLi0NJch/lQSJ1F9mhZffV2dJrIFl4ul7ngxgw2JPxKNr8vAEFI00HlaLBEm38Zy0A4sj4H/7iYTU+/0i7QQio+/AKJWGcOQw8ZHvfCG1L8GvxYUTp15N/Nn9UNyiBeAvrKEnz6vVTHQ16qJgS0N8wBvYipKIxY6JF483VS+PMsaxtpAi5uhKi4X9s="), "§eSerbius", UUID.randomUUID());

        npcManager.createNPC(new Location(SPAWN, 15.5, 99, -30.5, 90, 0), new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTU5MTYyOTc4Mzc2MSwKICAicHJvZmlsZUlkIiA6ICJiMGM5MjQ2ZTcyMDE0ZDI2YTc3NWFmNDIyMTZkOTUwMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJFbGl0ZURhcmtuZXNzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzZkMTg3MzkxZmM2ZmNjYzhhMDdmMmI2NGJiZGUyODgwN2NjZTZjOGZiNzA0MDZiY2U2ZTcxNDg2MDc0YzdjOGMiCiAgICB9CiAgfQp9", "Bgywd/32NsH1kHtHRSz7pxeXTfaOPjFOzLjAFkV+Vjdq4gqCuoEMhkLo8wVXYk8tmUkz53+6VcQjoQhheJ0kjBXWVbLdGO83apAbjveZ4hOezDatW9Vv8YbD8+9vjEFOs8HuPWB1b6630xZKqcNwQqE+Q4q3hJBRxtlnV/qw8SjsJDKaTA8gLtJ/xl2fa5WNprjIvfULr7RUrJCSfB+a9BZKKcESnQ6ta7id+sGw/j0v/0eBBoq975SW0MSdDTuknn1P64qZTzJfEuIv1etTj2F+Wl6JVrEyamtPFKSA+rltbh64Foz4ebIV0JxIkpjCMO3puwsB2P5NqzvbRLjS7Jr0sEgORGpZD4Gck02Q2+cIQ9wuPkiKGn9sO5fGY97gXB4p/zfVOqty56lUclp7nnuw962bTUD8Mkl1jJQWoKlWOjLNVC9FY9XaTMyaReMH4Xgu6FVMPdEzyrGKndJPD6mkwzWCC+QQaeAK6bgaOMewrdct6NLi0NJch/lQSJ1F9mhZffV2dJrIFl4ul7ngxgw2JPxKNr8vAEFI00HlaLBEm38Zy0A4sj4H/7iYTU+/0i7QQio+/AKJWGcOQw8ZHvfCG1L8GvxYUTp15N/Nn9UNyiBeAvrKEnz6vVTHQ16qJgS0N8wBvYipKIxY6JF483VS+PMsaxtpAi5uhKi4X9s="), "§eSerbia", UUID.randomUUID());

        npcManager.createNPC(new Location(SPAWN, -44.5, 99, -41.5, -50, 0), new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTU5MTYyOTc4Mzc2MSwKICAicHJvZmlsZUlkIiA6ICJiMGM5MjQ2ZTcyMDE0ZDI2YTc3NWFmNDIyMTZkOTUwMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJFbGl0ZURhcmtuZXNzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzZkMTg3MzkxZmM2ZmNjYzhhMDdmMmI2NGJiZGUyODgwN2NjZTZjOGZiNzA0MDZiY2U2ZTcxNDg2MDc0YzdjOGMiCiAgICB9CiAgfQp9", "Bgywd/32NsH1kHtHRSz7pxeXTfaOPjFOzLjAFkV+Vjdq4gqCuoEMhkLo8wVXYk8tmUkz53+6VcQjoQhheJ0kjBXWVbLdGO83apAbjveZ4hOezDatW9Vv8YbD8+9vjEFOs8HuPWB1b6630xZKqcNwQqE+Q4q3hJBRxtlnV/qw8SjsJDKaTA8gLtJ/xl2fa5WNprjIvfULr7RUrJCSfB+a9BZKKcESnQ6ta7id+sGw/j0v/0eBBoq975SW0MSdDTuknn1P64qZTzJfEuIv1etTj2F+Wl6JVrEyamtPFKSA+rltbh64Foz4ebIV0JxIkpjCMO3puwsB2P5NqzvbRLjS7Jr0sEgORGpZD4Gck02Q2+cIQ9wuPkiKGn9sO5fGY97gXB4p/zfVOqty56lUclp7nnuw962bTUD8Mkl1jJQWoKlWOjLNVC9FY9XaTMyaReMH4Xgu6FVMPdEzyrGKndJPD6mkwzWCC+QQaeAK6bgaOMewrdct6NLi0NJch/lQSJ1F9mhZffV2dJrIFl4ul7ngxgw2JPxKNr8vAEFI00HlaLBEm38Zy0A4sj4H/7iYTU+/0i7QQio+/AKJWGcOQw8ZHvfCG1L8GvxYUTp15N/Nn9UNyiBeAvrKEnz6vVTHQ16qJgS0N8wBvYipKIxY6JF483VS+PMsaxtpAi5uhKi4X9s="), "§cRoland", UUID.randomUUID());

        npcManager.createNPC(new Location(SPAWN, 3.5, 99, 146.5, 175, 0), new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTU5MTYyOTc4Mzc2MSwKICAicHJvZmlsZUlkIiA6ICJiMGM5MjQ2ZTcyMDE0ZDI2YTc3NWFmNDIyMTZkOTUwMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJFbGl0ZURhcmtuZXNzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzZkMTg3MzkxZmM2ZmNjYzhhMDdmMmI2NGJiZGUyODgwN2NjZTZjOGZiNzA0MDZiY2U2ZTcxNDg2MDc0YzdjOGMiCiAgICB9CiAgfQp9", "Bgywd/32NsH1kHtHRSz7pxeXTfaOPjFOzLjAFkV+Vjdq4gqCuoEMhkLo8wVXYk8tmUkz53+6VcQjoQhheJ0kjBXWVbLdGO83apAbjveZ4hOezDatW9Vv8YbD8+9vjEFOs8HuPWB1b6630xZKqcNwQqE+Q4q3hJBRxtlnV/qw8SjsJDKaTA8gLtJ/xl2fa5WNprjIvfULr7RUrJCSfB+a9BZKKcESnQ6ta7id+sGw/j0v/0eBBoq975SW0MSdDTuknn1P64qZTzJfEuIv1etTj2F+Wl6JVrEyamtPFKSA+rltbh64Foz4ebIV0JxIkpjCMO3puwsB2P5NqzvbRLjS7Jr0sEgORGpZD4Gck02Q2+cIQ9wuPkiKGn9sO5fGY97gXB4p/zfVOqty56lUclp7nnuw962bTUD8Mkl1jJQWoKlWOjLNVC9FY9XaTMyaReMH4Xgu6FVMPdEzyrGKndJPD6mkwzWCC+QQaeAK6bgaOMewrdct6NLi0NJch/lQSJ1F9mhZffV2dJrIFl4ul7ngxgw2JPxKNr8vAEFI00HlaLBEm38Zy0A4sj4H/7iYTU+/0i7QQio+/AKJWGcOQw8ZHvfCG1L8GvxYUTp15N/Nn9UNyiBeAvrKEnz6vVTHQ16qJgS0N8wBvYipKIxY6JF483VS+PMsaxtpAi5uhKi4X9s="), "§aTeya", UUID.randomUUID());
    }

}