package eu.darkcube.system.skyland.Equipment;

public class Test {
    public static void main(String[] args) {

        String s = "**test**hi**";
        for (String st: s.split("\\*\\*")) {
            System.out.println(st);
        }

    }
}
