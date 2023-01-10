package eu.darkcube.system.skyland.Equipment;

public class Components {

    Materials type;
    ComponentTypes compType;
    public PlayerStats[] getPStats(){
        //todo calc adding comp type
        return type.getStats();
    }

    public Components(Materials type, ComponentTypes compType) {
        this.type = type;
        this.compType = compType;
    }

    @Override
    public String toString() {
        return "Components{" +
                "type=**" + type +
                "** compType=**" + compType + "**"+
                '}';
    }

    public static Components parseFromString(String s){
        String[] temp = s.split("\\*\\*");
        return new Components(Materials.valueOf(temp[1]), ComponentTypes.valueOf(temp[3]));
    }

}
