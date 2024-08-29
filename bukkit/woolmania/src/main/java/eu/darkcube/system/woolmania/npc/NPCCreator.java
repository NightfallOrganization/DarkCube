package eu.darkcube.system.woolmania.npc;

import static eu.darkcube.system.woolmania.manager.WorldManager.HALLS;
import static eu.darkcube.system.woolmania.manager.WorldManager.SPAWN;

import java.util.UUID;

import eu.darkcube.system.woolmania.manager.NPCManager;
import org.bukkit.Location;

public class NPCCreator {
    private NPCManager npcManager;
    public static final String NAME_ZINUS = "Zinus";
    public static final String NAME_ZINA = "Zina";
    public static final String NAME_VARKAS = "Varkas";
    public static final String NAME_ASTAROTH = "Astaroth";
    public NPC zinus;
    public NPC zinus2;
    public NPC zinus3;
    public NPC zina;
    public NPC zina2;
    public NPC zina3;
    public NPC varkas;
    public NPC astaroth;

    public NPCCreator(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    public void createNPC() {
        createZinusNPC(new Location(HALLS, 13.5, 111, -20.5, 0, 0));
        createZinaNPC(new Location(HALLS, 13.5, 111, 21.5, -180, 0));

        createZinus2NPC(new Location(HALLS, 13.5, 111, 979.5, 0, 0));
        createZina2NPC(new Location(HALLS, 13.5, 111, 1021.5, -180, 0));

        createZinus3NPC(new Location(HALLS, 13.5, 111, 1979.5, 0, 0));
        createZina3NPC(new Location(HALLS, 13.5, 111, 2021.5, -180, 0));

        createVarkasNPC(new Location(SPAWN, 15.5, 199, 1.5, -180, 0));
        createAstarothNPC(new Location(SPAWN, 10.5, 199, -1.5, -90, 0));
    }

    private void createZinusNPC(Location location) {
        zinus = npcManager.createNPC(location, new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTcyMjUxMjMwMDYyMywKICAicHJvZmlsZUlkIiA6ICJlNzlkOGFlZDk0N2U0MThkODA0ZjhkYjRjYWM4NTE1MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJlYmxzc2RrIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkyMGQyODQxODc0OWM2NDc1NTcwYWNhMmYzNDY4OWUwZWE5MmVkNzE2YjIxZWI0NzgyZTRmNGE2ZDNhMWIyNGUiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                "mlPVruvu6T3M0c66yDkJ34XqNnBji1OOpc9T2ZPlR3iHt31ssDkR++aAzKht0vf3DZxKKWfXBTPTY7fQvzxTzKvSbt/a5euw0ZAeI9abdlKzrrJYglqIMvc6ANvpBUqwVXABvIFcuIUA1rY61gyWf9K84kmWTVipHGFfHu1M1oCES4MlFThisp/KO2GuZSh9Hwh9d6/KKJaeIVCE3u7egOgTj3hnSsPqYvZqTNJwjKu4Yz12q+1ETJ9iNYU2wuxQsmz/Z7LEEz6f+mHilfUqvRBqdSzS5gPB4J8ce+tZXztTk0BzAcyyKcXu/HdzDdAAh0WJtjZy/buZCS76WcFjufAsD7uzIG7EDwmLKC4VLdZoKM+O2Ny7orADi0oGmh87qAcsEoonvJaBtao5bwuZWn8DHs58nOHRw0nVhxEyOYSL/GhOB52OXVPPLoI94EUfT6Njco+EV0IPD1tQfua/jZLv4BeHIyXaJX3JlS8XSHjm7JeEEoRAEiv8XE+Q4Hai9vK5xBRqSPDJWYheoq94RE/08c6NNQxuhB1MfcxRL+vSMeZvgL5Z+ea4TkcjAQ3McnwFObCFbSE9wTrVORY0OSfmRS8MgwNmuZiRxT+S7lLmw5qc+ADgm6wsSuiTCPCoc0KUUl0X55HEm5JZZRacmbM5jL/PMU4mCqpE80muEjY="), NPCCreator.NAME_ZINUS, UUID.randomUUID());
    }

    private void createZinus2NPC(Location location) {
        zinus2 = npcManager.createNPC(location, new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTcyMjUxMjMwMDYyMywKICAicHJvZmlsZUlkIiA6ICJlNzlkOGFlZDk0N2U0MThkODA0ZjhkYjRjYWM4NTE1MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJlYmxzc2RrIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkyMGQyODQxODc0OWM2NDc1NTcwYWNhMmYzNDY4OWUwZWE5MmVkNzE2YjIxZWI0NzgyZTRmNGE2ZDNhMWIyNGUiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                "mlPVruvu6T3M0c66yDkJ34XqNnBji1OOpc9T2ZPlR3iHt31ssDkR++aAzKht0vf3DZxKKWfXBTPTY7fQvzxTzKvSbt/a5euw0ZAeI9abdlKzrrJYglqIMvc6ANvpBUqwVXABvIFcuIUA1rY61gyWf9K84kmWTVipHGFfHu1M1oCES4MlFThisp/KO2GuZSh9Hwh9d6/KKJaeIVCE3u7egOgTj3hnSsPqYvZqTNJwjKu4Yz12q+1ETJ9iNYU2wuxQsmz/Z7LEEz6f+mHilfUqvRBqdSzS5gPB4J8ce+tZXztTk0BzAcyyKcXu/HdzDdAAh0WJtjZy/buZCS76WcFjufAsD7uzIG7EDwmLKC4VLdZoKM+O2Ny7orADi0oGmh87qAcsEoonvJaBtao5bwuZWn8DHs58nOHRw0nVhxEyOYSL/GhOB52OXVPPLoI94EUfT6Njco+EV0IPD1tQfua/jZLv4BeHIyXaJX3JlS8XSHjm7JeEEoRAEiv8XE+Q4Hai9vK5xBRqSPDJWYheoq94RE/08c6NNQxuhB1MfcxRL+vSMeZvgL5Z+ea4TkcjAQ3McnwFObCFbSE9wTrVORY0OSfmRS8MgwNmuZiRxT+S7lLmw5qc+ADgm6wsSuiTCPCoc0KUUl0X55HEm5JZZRacmbM5jL/PMU4mCqpE80muEjY="), NPCCreator.NAME_ZINUS, UUID.randomUUID());
    }

    private void createZinus3NPC(Location location) {
        zinus3 = npcManager.createNPC(location, new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTcyMjUxMjMwMDYyMywKICAicHJvZmlsZUlkIiA6ICJlNzlkOGFlZDk0N2U0MThkODA0ZjhkYjRjYWM4NTE1MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJlYmxzc2RrIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkyMGQyODQxODc0OWM2NDc1NTcwYWNhMmYzNDY4OWUwZWE5MmVkNzE2YjIxZWI0NzgyZTRmNGE2ZDNhMWIyNGUiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                "mlPVruvu6T3M0c66yDkJ34XqNnBji1OOpc9T2ZPlR3iHt31ssDkR++aAzKht0vf3DZxKKWfXBTPTY7fQvzxTzKvSbt/a5euw0ZAeI9abdlKzrrJYglqIMvc6ANvpBUqwVXABvIFcuIUA1rY61gyWf9K84kmWTVipHGFfHu1M1oCES4MlFThisp/KO2GuZSh9Hwh9d6/KKJaeIVCE3u7egOgTj3hnSsPqYvZqTNJwjKu4Yz12q+1ETJ9iNYU2wuxQsmz/Z7LEEz6f+mHilfUqvRBqdSzS5gPB4J8ce+tZXztTk0BzAcyyKcXu/HdzDdAAh0WJtjZy/buZCS76WcFjufAsD7uzIG7EDwmLKC4VLdZoKM+O2Ny7orADi0oGmh87qAcsEoonvJaBtao5bwuZWn8DHs58nOHRw0nVhxEyOYSL/GhOB52OXVPPLoI94EUfT6Njco+EV0IPD1tQfua/jZLv4BeHIyXaJX3JlS8XSHjm7JeEEoRAEiv8XE+Q4Hai9vK5xBRqSPDJWYheoq94RE/08c6NNQxuhB1MfcxRL+vSMeZvgL5Z+ea4TkcjAQ3McnwFObCFbSE9wTrVORY0OSfmRS8MgwNmuZiRxT+S7lLmw5qc+ADgm6wsSuiTCPCoc0KUUl0X55HEm5JZZRacmbM5jL/PMU4mCqpE80muEjY="), NPCCreator.NAME_ZINUS, UUID.randomUUID());
    }

    private void createZinaNPC(Location location) {
        zina = npcManager.createNPC(location, new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTcyMTk5ODI3MzU4NiwKICAicHJvZmlsZUlkIiA6ICI1ZDI0YmEwYjI4OGM0MjkzOGJhMTBlYzk5MDY0ZDI1OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4RmFpaUxlUiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jMmUzNGMzNjM5ZjA4OWU0ZmU0YTQ5MjFmYWVjZDhjMjQ0MWY4N2NiZjNlY2Q4NTI4OWVjYTk5OTU0ODBkNWM5IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                "h5DgBzCMqartSoxBn7q7ij/DDQ80rNd54CMYR/AMTKI4BzDwGslb4j3I2bxxKlS7DODkkwO4hQPsQtQ0PgCGf3ZrhQFrKv2CZ4sVdZ24hvB2851FME3J45NOO9c2jV66YefWgUxgUrrYr5q9ipquk8wjqokQN33WP6XoRzD+8YyzNHVCx3ItR1FyvvH6L0eJD8XnxcKBA/b2p4kcSbNtgp7pdS6cqPcQ3iVrOeO5Qo8yLR+OJBnpi0rrBEmAKRFBH9OGzxhg+EKh+kY2hmzjlQQ0q+0m6ejTJHXeRdL3BjVPByQxodeH5Zrddj1Mu38/02znZgoweGUcMmmefPINQ2Hz8s1/LY6FzBbi9mEQpAFZOlUtS4Kcz9yjCRWrNHhOpD14lyOEMAUJvadrCnAKdkjkORjWjjQE8nCwNpbcUrOjYRs+xWoiL0JX9Flw34qdJZb3cUWPQZsomn+UWEfi9txml1DDhonvAqWhU0vUB1WMSWlb5MP+jjO8DWNHSZT57Hh136egNhXsyxO3E6OQGkomWLUag7fzsdLh+A6+TN/+7SjjknYZeJWPj9FQ4XGYEPIcAWh7jfLPdPtWDE8eFjdDa/LZUOCiKCOB8SeKfxZC4qIhFdKST+Tpo4p+dj8kIC95jSb0CZ3kOHBb9d+Mzq13txBCFnhXdwjk2w/iTxQ="), NPCCreator.NAME_ZINA, UUID.randomUUID());
    }

    private void createZina2NPC(Location location) {
        zina2 = npcManager.createNPC(location, new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTcyMTk5ODI3MzU4NiwKICAicHJvZmlsZUlkIiA6ICI1ZDI0YmEwYjI4OGM0MjkzOGJhMTBlYzk5MDY0ZDI1OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4RmFpaUxlUiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jMmUzNGMzNjM5ZjA4OWU0ZmU0YTQ5MjFmYWVjZDhjMjQ0MWY4N2NiZjNlY2Q4NTI4OWVjYTk5OTU0ODBkNWM5IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                "h5DgBzCMqartSoxBn7q7ij/DDQ80rNd54CMYR/AMTKI4BzDwGslb4j3I2bxxKlS7DODkkwO4hQPsQtQ0PgCGf3ZrhQFrKv2CZ4sVdZ24hvB2851FME3J45NOO9c2jV66YefWgUxgUrrYr5q9ipquk8wjqokQN33WP6XoRzD+8YyzNHVCx3ItR1FyvvH6L0eJD8XnxcKBA/b2p4kcSbNtgp7pdS6cqPcQ3iVrOeO5Qo8yLR+OJBnpi0rrBEmAKRFBH9OGzxhg+EKh+kY2hmzjlQQ0q+0m6ejTJHXeRdL3BjVPByQxodeH5Zrddj1Mu38/02znZgoweGUcMmmefPINQ2Hz8s1/LY6FzBbi9mEQpAFZOlUtS4Kcz9yjCRWrNHhOpD14lyOEMAUJvadrCnAKdkjkORjWjjQE8nCwNpbcUrOjYRs+xWoiL0JX9Flw34qdJZb3cUWPQZsomn+UWEfi9txml1DDhonvAqWhU0vUB1WMSWlb5MP+jjO8DWNHSZT57Hh136egNhXsyxO3E6OQGkomWLUag7fzsdLh+A6+TN/+7SjjknYZeJWPj9FQ4XGYEPIcAWh7jfLPdPtWDE8eFjdDa/LZUOCiKCOB8SeKfxZC4qIhFdKST+Tpo4p+dj8kIC95jSb0CZ3kOHBb9d+Mzq13txBCFnhXdwjk2w/iTxQ="), NPCCreator.NAME_ZINA, UUID.randomUUID());
    }

    private void createZina3NPC(Location location) {
        zina3 = npcManager.createNPC(location, new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTcyMTk5ODI3MzU4NiwKICAicHJvZmlsZUlkIiA6ICI1ZDI0YmEwYjI4OGM0MjkzOGJhMTBlYzk5MDY0ZDI1OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4RmFpaUxlUiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jMmUzNGMzNjM5ZjA4OWU0ZmU0YTQ5MjFmYWVjZDhjMjQ0MWY4N2NiZjNlY2Q4NTI4OWVjYTk5OTU0ODBkNWM5IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                "h5DgBzCMqartSoxBn7q7ij/DDQ80rNd54CMYR/AMTKI4BzDwGslb4j3I2bxxKlS7DODkkwO4hQPsQtQ0PgCGf3ZrhQFrKv2CZ4sVdZ24hvB2851FME3J45NOO9c2jV66YefWgUxgUrrYr5q9ipquk8wjqokQN33WP6XoRzD+8YyzNHVCx3ItR1FyvvH6L0eJD8XnxcKBA/b2p4kcSbNtgp7pdS6cqPcQ3iVrOeO5Qo8yLR+OJBnpi0rrBEmAKRFBH9OGzxhg+EKh+kY2hmzjlQQ0q+0m6ejTJHXeRdL3BjVPByQxodeH5Zrddj1Mu38/02znZgoweGUcMmmefPINQ2Hz8s1/LY6FzBbi9mEQpAFZOlUtS4Kcz9yjCRWrNHhOpD14lyOEMAUJvadrCnAKdkjkORjWjjQE8nCwNpbcUrOjYRs+xWoiL0JX9Flw34qdJZb3cUWPQZsomn+UWEfi9txml1DDhonvAqWhU0vUB1WMSWlb5MP+jjO8DWNHSZT57Hh136egNhXsyxO3E6OQGkomWLUag7fzsdLh+A6+TN/+7SjjknYZeJWPj9FQ4XGYEPIcAWh7jfLPdPtWDE8eFjdDa/LZUOCiKCOB8SeKfxZC4qIhFdKST+Tpo4p+dj8kIC95jSb0CZ3kOHBb9d+Mzq13txBCFnhXdwjk2w/iTxQ="), NPCCreator.NAME_ZINA, UUID.randomUUID());
    }

    private void createVarkasNPC(Location location) {
        varkas = npcManager.createNPC(location, new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTYwMjcyMzkxNTMyMSwKICAicHJvZmlsZUlkIiA6ICJiMGQ0YjI4YmMxZDc0ODg5YWYwZTg2NjFjZWU5NmFhYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5lU2tpbl9vcmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmQzNWI4ZDc0NDE2NmQ1YTYzMmQxMTM5ZTc1ZjMzZjc5NmY0YTNjZTIyNzFlYWY3NDRlNDEwYmI5NTEyYTllMCIKICAgIH0KICB9Cn0=",
                "tJGhwFUZNSrG8NHmkshiALcZ8NkaCPcm0Y4uyer9wnp+C3+TIPJjmA2BtqhtZ+Vnulja2xGHGhyC94huSSnXkJBShKexDmrUjgD4mXdBKt9PCFxTdNUp7yiFAL49V3D2kD/Sj5CGupBORYhbDMa4emwCnBgxtUK3tfrMyaX1ZHVbK1X5zDU3XDmYbT02LY3185xmLw0UohW0GwvwhEq97EjG30G5pbUH6E2ZZDlN0VqSA6zRNdwYN5SejALWzDSOG9cJ+KpLAogxHDGZsveOP08dZyqbECiUPamx5MUXQ8LHzkKKiunR18hFtjmSgSviMOLlnNMzxgn1oneFe/YRqBN8MyG6RHPWSh3s3gy0yKD33aiPLbJHWi9jmKTSC/0Cbt99biit9p/UAwtJJ7Hzf2m1/ue6JkP+hmFIxSVTjjHRymWTL2GX7CcAsq07HfHFHC5YjQCxY3QnAW/HfWzxjCoX34OBJa8snNwEaG70Kq/c86PuQaorcNdH4QRA6RWp7WAk3SaBpc93UYyDxpm4nDos2hQZxoIYxxyy6KKCTDTLcGo0OgRek/iKrDQHLLdLxl3jvnqbUgss9OU0ynuKfjzml9TCg4s1JYHEdgmNrmkQnonWkXvDpq5xOWPRoC0FAcJJtCgJv7UvUx5l+IqBR6Nyxv7rCxiyFE2K4UHeZbI="), NPCCreator.NAME_VARKAS, UUID.randomUUID());
    }

    private void createAstarothNPC(Location location) {
        astaroth = npcManager.createNPC(location, new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTU4ODQzODIzMDA4NiwKICAicHJvZmlsZUlkIiA6ICI0NDAzZGM1NDc1YmM0YjE1YTU0OGNmZGE2YjBlYjdkOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJGbGF3Q3JhQm90MDEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjA1OTc3MGU2ODc0ZjM0YTBkZmJjNTZkMTQ2MjQ2YzUxODY2YThmM2FjMmQ2NTAzMzJlZTlkY2Q2MmVlY2NhMyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
                "q+cXEv8LpZ+4EsWBqJ5lg5Ukn7r3UBHM8RgAtq4gQwzdBKgPITAsayJsuTggv7vE3iN5eeqCntRxnp3N6A3B40h1AeRuyS8irWywXv6ptXVxNv1tg6w3+hb4VhZhQLIqYP/fkFyYh5bOylyaDslyzfvGln4r7oNHswa7w/00g3nowOENFLsqhtlXwdeR92uQMjkWkbuHu1QnPGOTIQTvlaEWLeVj/WPZKkqPS5yu2vlI0VEXYHAQrJn1nmDUjp4y4av+szCvBtMgR7NKTPd0c1g2O8dAn/OrCLXuZa87tMdAgrqIHDqMNXWXRF9ZFctd19NtGd1o13nzFicIP3kMIY4dKQreKk7yaqIK1ddYkkpmqoffcqonQQk2YKDRYHhdCdL8eXODGwXshAA4nfABcj+RjG+VLaRjSF+f5O2rEupM8PrT+wvj6ESuqp22s+nPffKWdaFZVVCRZN07OoWCzbnbrRYJNG0pK259vyjXBQqXX9KolVyC7lxq3fwgPjQSSIFtmhJ1D/sT4oq68DKNzoJ1TNHZAL7OxD7SXJTsCIR2lnE2Dj08ZmLDCZwVD8tsvznXgY8fQZZtqpGwpxzyKiGT96at9cMF80BVHDE47uS+zmw5BA9AeuImEEoxgp1tslQ7l732DL0bdWiJUN1XH4FaA9heWAcH0RspzwthKYU="), NPCCreator.NAME_ASTAROTH, UUID.randomUUID());
    }
}
