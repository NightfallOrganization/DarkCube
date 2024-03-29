package eu.darkcube.minigame.woolbattle.common.user;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.user.PerksStorage;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;

public class CommonPerksStorage implements PerksStorage {
    private static final PersistentDataType<PerkName[]> PERK_NAME_ARRAY = PersistentDataTypes.array(PerkName.TYPE, PerkName.class);
    public static final PersistentDataType<PerksStorage> TYPE = new PersistentDataType<>() {
        @Override
        public CommonPerksStorage deserialize(Document doc, String key) {
            var d = doc.readDocument(key);
            var docPerks = d.readDocument("perks");
            var docPerkSlots = d.readDocument("perkSlots");
            var perks = new HashMap<ActivationType, PerkName[]>();
            var perkSlots = new HashMap<ActivationType, int[]>();
            for (var documentKey : docPerks.keys()) {
                var type = ActivationType.valueOf(documentKey);
                var array = PERK_NAME_ARRAY.deserialize(docPerks, documentKey);
                perks.put(type, array);
            }
            for (var documentKey : docPerkSlots.keys()) {
                var type = ActivationType.valueOf(documentKey);
                var array = PersistentDataTypes.INT_ARRAY.deserialize(docPerkSlots, documentKey);
                perkSlots.put(type, array);
            }
            return new CommonPerksStorage(perks, perkSlots);
        }

        @Override
        public void serialize(Document.Mutable doc, String key, PerksStorage data) {
            var d = Document.newJsonDocument();
            var docPerks = Document.newJsonDocument();
            var docPerkSlots = Document.newJsonDocument();
            for (var type : ActivationType.values()) {
                PERK_NAME_ARRAY.serialize(docPerks, type.name(), data.perks(type));
                PersistentDataTypes.INT_ARRAY.serialize(docPerks, type.name(), data.perkInvSlots(type));
            }
            d.append("perks", docPerks);
            d.append("perkSlots", docPerkSlots);
            doc.append(key, d);
        }

        @Override
        public PerksStorage clone(PerksStorage object) {
            return object.clone();
        }
    };
    private final Map<ActivationType, PerkName[]> perks;
    private final Map<ActivationType, int[]> perkSlots;

    public CommonPerksStorage() {
        var perks = new HashMap<ActivationType, PerkName[]>();
        var perkSlots = new HashMap<ActivationType, int[]>();
        for (var type : ActivationType.values()) {
            perks.put(type, new PerkName[type.maxCount()]);
            perkSlots.put(type, new int[type.maxCount()]);
        }
        this.perks = Collections.unmodifiableMap(perks);
        this.perkSlots = Collections.unmodifiableMap(perkSlots);
        reset();
    }

    public CommonPerksStorage(Map<ActivationType, PerkName[]> perks, Map<ActivationType, int[]> perkSlots) {
        this();
        perks(perks);
        for (var entry : perkSlots.entrySet()) {
            System.arraycopy(entry.getValue(), 0, this.perkSlots.get(entry.getKey()), 0, Math.min(entry.getValue().length, this.perkSlots.get(entry.getKey()).length));
        }
    }

    @Override
    public CommonPerksStorage clone() {
        return new CommonPerksStorage(perks, perkSlots);
    }

    @Override
    public PerkName[] perks(ActivationType type) {
        return perks.get(type).clone();
    }

    @Override
    public PerkName perk(ActivationType type, int perkSlot) {
        return perks.get(type)[perkSlot];
    }

    @Override
    public void perk(ActivationType type, int perkSlot, PerkName perk) {
        perks.get(type)[perkSlot] = perk;
    }

    @Override
    public int[] perkInvSlots(ActivationType type) {
        return perkSlots.get(type).clone();
    }

    @Override
    public int perkInvSlot(ActivationType type, int perkSlot) {
        return perkSlots.get(type)[perkSlot];
    }

    @Override
    public void perkInvSlot(ActivationType type, int perkSlot, int slot) {
        perkSlots.get(type)[perkSlot] = slot;
    }

    @Override
    public Map<ActivationType, PerkName[]> perks() {
        return perks;
    }

    @Override
    public void perks(Map<ActivationType, PerkName[]> perks) {
        for (var entry : perks.entrySet()) {
            System.arraycopy(entry.getValue(), 0, this.perks.get(entry.getKey()), 0, Math.min(entry.getValue().length, this.perks.get(entry.getKey()).length));
        }
    }

    @Override
    public void reset() {
        var list = Arrays.asList(ActivationType.values());
        Collections.sort(list);
        var slotsUsed = new HashSet<Integer>();
        var slotsQueryFrom = new ArrayDeque<Integer>();

    }
}
