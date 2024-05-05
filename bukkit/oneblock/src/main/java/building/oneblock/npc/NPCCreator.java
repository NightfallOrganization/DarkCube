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
        npcManager.createNPC(new Location(SPAWN, 35.5, 99, -8.5, 0, 0), new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTcxNDE3NTUzMDQzNiwKICAicHJvZmlsZUlkIiA6ICJhN2E4YWY1NTk0ZGQ0OTJjYWViNjc1NzUzMzRlNmM0OCIsCiAgInByb2ZpbGVOYW1lIiA6ICJpdHNUcmFjZXJvdXRlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2IxNTRjZWM5ZGJmZWU3Mjg4OGJlYWNmZGRkNGE1ZjU5ZThhOTE4NmU2NjMwZDFmNzM3ZDI4OTM4ZjU3NmYyNDciCiAgICB9CiAgfQp9", "XAJ9C5WU8yQQP2Qvtc5gpaZ4P8MA59nDoGNfYHQtsusiyECwrdeWe/0BOdDmp7/7RC1IMA3IOz1J0nF0ovcDsn+dGuqo4OXtMaU16jeaKC6KRq26922x0EGs4PQWD0flOCxJnMaCoBh22FiBS4uQ9ukOQjBxq3d+LAEj6QQhdXINh9ozhZokQk5J1vPMiw2kTQZoLygnYUGfFHCQMtTZuMU/Me0Nfd1XbhLcHHqNjEaxxsNPdaJhgtv0rFU1t1p6pkihgQptGoo6Eu5QqF2xcQZXHmDSVH6mMfVhFAoO5jChxWj1IWhwbmCw1pRL+/2mfJecfKhJN8io/aAWyiO2B3sXMfKQv/6r852fvAbk4kn7yPXTKkl3PUmIC1GQ7ZvnW6j4KZggPLgfQKVH1HXypcrznh/b4uYnX61vkiHURjvEdoT9exe0Ho9C6GxR65j1ZRllU7UUY4jc62j+a5zjPIv6qaT22ifnRHr5o2Zm/tHjXeU4PoqDC8FI3Itzd9+conYZ6mdZcepGJC6Zjmwr/hK9m1alEHPmHlUlfjO6Rxv9prCIezfhAFSfukJRbzS9nPZV3w9nHYBHP8cP5dthkk8YhaddkcojZ4HUo8jvcZreizWocWFzEITNZtkrcfE778nYLGXYvlCWSFERWZ4EBJ8dldJiZKVUJ2ej1YLtZ3c="), "§eSerbius", UUID.randomUUID());

        npcManager.createNPC(new Location(SPAWN, 15.5, 99, -30.5, 90, 0), new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTcxNDE3NTQ4NDU0NSwKICAicHJvZmlsZUlkIiA6ICJhMmI1ZjhlM2MxZDI0ZmUzYTlkMzNiZTFhNzEzYTUwYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJOYXBpb0dvZCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iODQzMWQ0Y2M4N2FiYmJkMzBiZDUzNmM1ODJiMzJhMDU3NDllYWQxNjlmNmY0N2JjMTg1YTA2N2E3OWFlNWI1IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=", "HoJogq18Di5UUi0EHkahWqm1uU/6RohKYWjDLCd+XIE1AkCsj1VliokX/xGpYY+Vu5zovy+Iz4F/2lEzzPMRE0F3Y6HjbsdHcXoxMilYYyXz2qCFcGoGB37RcH6R72varlyNDLKxvRwM8qvYp3myG7/NKYd1kuSHcvGNKb55H24WLFkdwC9/IdIse4I24V5RoqySa+OYgxiDqEcH/PxGy7ilREprpct+YKuxbPNx801BDn3+oyyc5xYV+9kP/80ORR/fC0CElRVjnvZ5Vv48E7TwqVB1mgRUkXpsBgKrUQ2JP3+331gu2IYBm/MqWIpdS3HURZPXP8hZ4c98wSoXk/vVtBJ5gGoq3tfKqYimpH+mNPHaNJYQ/0pt9qmgpXYzdnT9uaKBV+/5gQNSv3xG/5B7qX+p08emkc815U3BY/zwl0a5pGPgsjla+aF4NvQsUGbo7GsLQbez3iJiJB4AUuc3nuYCtP+Dj7sBU6FkIcw33EPir3pOlic+fXxmcDLS7jX2iWBZrxKJvs52mwoDMkQa2iA86A9fUvJgjmV1e/Zb9pXty/eLoSwz7AZyh0hQLObG2hFsRzRQ3Wkjkq7622Q4TvTfv99Howv9bsDC9TLFd0CUDbo/jzHB+cLZZFxo6V6yxbk8Iek4FAkrnZpcQIbrQPfu11NlIg1KwVuiJeg="), "§eSerbia", UUID.randomUUID());

        npcManager.createNPC(new Location(SPAWN, -44.5, 99, -41.5, -50, 0), new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTcxMzkwNjM4MDA3NSwKICAicHJvZmlsZUlkIiA6ICJiNzQ4YWExODk3ZGU0Y2RiYjFhNTI1YjVjMjM3ZDc2MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaXhpOCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80MWE5NmJiNWNjMjRlNDA0MjU2MzZmNDQxM2JmYTNjY2IyZjBmY2JjODY4ZjMwYzNkZWNlNmI0MzQ1NmY3ZmI2IgogICAgfQogIH0KfQ==", "FbFNKP6jm8VLsZBuTDgdwpGX1EJkIJFdvDgt2iOlsW3cTt+aeB3e1RYV1/Gsaa789iT7+M4DkLSx5PUvmBR76ENQeo/GUobQ4cqnhoaooXsDLz1Y9eLAVnWETanKDbmW8GbuYKkrjR9EZIcEAdOQV8dlrOtXuo+mhaiAWcyPZlxsCPzjNMrBm/cwcaEkrDfkyZSPbN1KytRfmvVdliI4G0vw4BPse+ZOnM/2Yt/75b0GQYEl+tYd8Ei4rONTS04Ub1klgow8o5iTrml/XUGfIoYdr4YNVtCWnwvQtI3gqT80JwtIuoZkOg7P7kPaUSclMd5ro7dvsHWmw2Z29bzXk4JMVR9r352gbOcRiQkap8mAisifgaWjX42Uili/DlJ091ROwUMDvfMciOMZZwb5NLS7B5fFuIYmZd3kSig//02eSBCnIdVJBgfUiBlI0gQuCY4KQI1qMuvNion+50xrUELmWH/47Muy8CaB+i0bBz4jAhL4Txxi/r3OwZ8NuUW0HiVxNbzeFGP4ChfS/MhhNYXlXTXSfDnmfMGQ0kmi5K71jhX1pxk+Nj/1Ea3dm6PQP69Out3+a9DImC8LkZQfenKtTLIM7A351xTgRN3e3k7vfMKf7GqfGMJbRQGC8ENN6wfW3uO7c4PguIghgxWfdT7vPU64vRUP9+6ekcZhQhY="), "§cRolan", UUID.randomUUID());

        npcManager.createNPC(new Location(SPAWN, 3.5, 99, 146.5, 175, 0), new NPCManager.Skin("ewogICJ0aW1lc3RhbXAiIDogMTcxNDE3NjIwMzkyNCwKICAicHJvZmlsZUlkIiA6ICI5ZjY2ZGE5OTEwOWM0OWM2YWE2MWY0MjdkNjk3MzY0YSIsCiAgInByb2ZpbGVOYW1lIiA6ICJRdWNlbk1DIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzNhZTU4MGRiNmM3YzI1MDM2ZWY4MWMzMmRkMDQ4MDUwZmU5OTVlZjJiNWVjZjM5NGQ1MDBkZTE2Njg5OTM5MDEiCiAgICB9CiAgfQp9", "QNv0ah94sfB+iSfTpiGo1iHYtEtX3yZngGJBcfZGWYNIKfDuddqd2vJVpu04y86Qh1SJREyJwsL0l/dRctp/JPobqE734AiP6Hn5TWb/VihU/yAuRAjUaDvIjOrKlc0+nUTeqb8zDc3HOiO0aNtlBu3z/7lCSnR1MFel3EomHQ/yVEvAgSMe+GtVZM2n2UAJY1hoBcokesZUCQvI70GGo3UGZDPFxGhmLBxytRUFONN0ztr1dJ2fcF8ZBEfK4x+V71+wylGGxyJsIy+g8/4pXXMi6+hyOp7QeWZf1s2SU7wU6EKhy+sAQdBdQcT0B82hcpBnwpLDjplnjKwEnzJVNbYWzO1iPQm5ulGXgIBRza9SweyG8TQRRz5BR8X6KBo0tT/Ljo+EhT0D/C4GVEOrOFDTAIf/LyuR+ivSLaS+qRi/DzWUzhg23AOFx7huVcJrTiFjORyzFx13Ybo4Ym3D8V4FA+0m6eyfaPhxY8O18Wt9mCGsCnJth0qoeHms873nunEsbnE+9wOkIgiCGZYfPD1eSrKH3zTjbwSwFKf8JH86ivNv/nnQ08e46iQK0BU8naLw6thw8f5xCvvO/Nto2gQ1Ct5xRz28KSE/82XJzv0UcP4/z9FmeU0KLZ/Oo/bcnbDOcluxzIjK/cBcL3iYwkT9VZ0rdoMc87juVAT1x/A="), "§aTeya", UUID.randomUUID());
    }

}
