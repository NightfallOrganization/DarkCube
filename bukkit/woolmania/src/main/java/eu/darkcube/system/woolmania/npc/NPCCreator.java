package eu.darkcube.system.woolmania.npc;

import static eu.darkcube.system.woolmania.manager.WorldManager.HALLS;

import java.util.UUID;

import eu.darkcube.system.woolmania.manager.NPCManager;
import org.bukkit.Location;

public class NPCCreator {
    private NPCManager npcManager;
    public static final String NAME_ZINUS = "Zinus";
    public static final String NAME_ZINA = "Zina";
    public NPC zinus;
    public NPC zina;

    public NPCCreator(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    public void createNPC() {
        zinus = npcManager.createNPC(new Location(HALLS, 13.5, 111, -20.5, 0, 0), new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTcyMjUxMjMwMDYyMywKICAicHJvZmlsZUlkIiA6ICJlNzlkOGFlZDk0N2U0MThkODA0ZjhkYjRjYWM4NTE1MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJlYmxzc2RrIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkyMGQyODQxODc0OWM2NDc1NTcwYWNhMmYzNDY4OWUwZWE5MmVkNzE2YjIxZWI0NzgyZTRmNGE2ZDNhMWIyNGUiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                "mlPVruvu6T3M0c66yDkJ34XqNnBji1OOpc9T2ZPlR3iHt31ssDkR++aAzKht0vf3DZxKKWfXBTPTY7fQvzxTzKvSbt/a5euw0ZAeI9abdlKzrrJYglqIMvc6ANvpBUqwVXABvIFcuIUA1rY61gyWf9K84kmWTVipHGFfHu1M1oCES4MlFThisp/KO2GuZSh9Hwh9d6/KKJaeIVCE3u7egOgTj3hnSsPqYvZqTNJwjKu4Yz12q+1ETJ9iNYU2wuxQsmz/Z7LEEz6f+mHilfUqvRBqdSzS5gPB4J8ce+tZXztTk0BzAcyyKcXu/HdzDdAAh0WJtjZy/buZCS76WcFjufAsD7uzIG7EDwmLKC4VLdZoKM+O2Ny7orADi0oGmh87qAcsEoonvJaBtao5bwuZWn8DHs58nOHRw0nVhxEyOYSL/GhOB52OXVPPLoI94EUfT6Njco+EV0IPD1tQfua/jZLv4BeHIyXaJX3JlS8XSHjm7JeEEoRAEiv8XE+Q4Hai9vK5xBRqSPDJWYheoq94RE/08c6NNQxuhB1MfcxRL+vSMeZvgL5Z+ea4TkcjAQ3McnwFObCFbSE9wTrVORY0OSfmRS8MgwNmuZiRxT+S7lLmw5qc+ADgm6wsSuiTCPCoc0KUUl0X55HEm5JZZRacmbM5jL/PMU4mCqpE80muEjY="), NAME_ZINUS, UUID.randomUUID());

        zina = npcManager.createNPC(new Location(HALLS, 13.5, 111, 21.5, -180, 0), new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTcyMTk5ODI3MzU4NiwKICAicHJvZmlsZUlkIiA6ICI1ZDI0YmEwYjI4OGM0MjkzOGJhMTBlYzk5MDY0ZDI1OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4RmFpaUxlUiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jMmUzNGMzNjM5ZjA4OWU0ZmU0YTQ5MjFmYWVjZDhjMjQ0MWY4N2NiZjNlY2Q4NTI4OWVjYTk5OTU0ODBkNWM5IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                "h5DgBzCMqartSoxBn7q7ij/DDQ80rNd54CMYR/AMTKI4BzDwGslb4j3I2bxxKlS7DODkkwO4hQPsQtQ0PgCGf3ZrhQFrKv2CZ4sVdZ24hvB2851FME3J45NOO9c2jV66YefWgUxgUrrYr5q9ipquk8wjqokQN33WP6XoRzD+8YyzNHVCx3ItR1FyvvH6L0eJD8XnxcKBA/b2p4kcSbNtgp7pdS6cqPcQ3iVrOeO5Qo8yLR+OJBnpi0rrBEmAKRFBH9OGzxhg+EKh+kY2hmzjlQQ0q+0m6ejTJHXeRdL3BjVPByQxodeH5Zrddj1Mu38/02znZgoweGUcMmmefPINQ2Hz8s1/LY6FzBbi9mEQpAFZOlUtS4Kcz9yjCRWrNHhOpD14lyOEMAUJvadrCnAKdkjkORjWjjQE8nCwNpbcUrOjYRs+xWoiL0JX9Flw34qdJZb3cUWPQZsomn+UWEfi9txml1DDhonvAqWhU0vUB1WMSWlb5MP+jjO8DWNHSZT57Hh136egNhXsyxO3E6OQGkomWLUag7fzsdLh+A6+TN/+7SjjknYZeJWPj9FQ4XGYEPIcAWh7jfLPdPtWDE8eFjdDa/LZUOCiKCOB8SeKfxZC4qIhFdKST+Tpo4p+dj8kIC95jSb0CZ3kOHBb9d+Mzq13txBCFnhXdwjk2w/iTxQ="), NAME_ZINA, UUID.randomUUID());
    }

}
