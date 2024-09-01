/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums;

public enum Tiers {
    // DEFAULT_TIER(1, "Default Tier"),
    TIER1(1, "Tier I", 150),
    TIER2(2, "Tier II", 500),
    TIER3(3, "Tier III", 850),
    TIER4(4, "Tier IV", 1200),
    TIER5(5, "Tier V", 1550),
    TIER6(6, "Tier VI", 1900),
    TIER7(7, "Tier VII", 2250),
    TIER8(8, "Tier VIII", 2600),
    TIER9(9, "Tier IX", 2950),
    TIER10(10, "Tier X", 3300),
    TIER11(11, "Tier XI", 3650),
    TIER12(12, "Tier XII", 4000),
    TIER13(13, "Tier XIII", 4350),
    TIER14(14, "Tier XIV", 4700),
    TIER15(15, "Tier XV", 5050),
    TIER16(15, "Tier XVI", 5400),
    TIER17(17, "Tier XVII", 5750),
    TIER18(18, "Tier XVIII", 6100),
    TIER19(19, "Tier XIX", 6450),
    TIER20(20, "Tier XX", 6800),
    TIER21(21, "Tier XXI", 7150),
    TIER22(22, "Tier XXII", 7500),
    TIER23(23, "Tier XXIII", 7850),
    TIER24(24, "Tier XXIV", 8200),
    TIER25(25, "Tier XXV", 8550),
    TIER26(26, "Tier XXVI", 8900),
    TIER27(27, "Tier XXVII", 9250),
    TIER28(28, "Tier XXVIII", 9600),
    TIER29(29, "Tier XXIX", 9950),
    TIER30(30, "Tier XXX", 10300),
    TIER31(31, "Tier XXXI", 10650),
    TIER32(32, "Tier XXXII", 11000),
    TIER33(33, "Tier XXXIII", 11350),
    TIER34(34, "Tier XXXIV", 11700),
    TIER35(35, "Tier XXXV", 12050),
    TIER36(36, "Tier XXXVI", 12400),
    TIER37(37, "Tier XXXVII", 12750),
    TIER38(38, "Tier XXXVIII", 13100),
    TIER39(39, "Tier XXXIX", 13450),
    TIER40(40, "Tier XL", 13800),
    TIER41(41, "Tier XLI", 14150),
    TIER42(42, "Tier XLII", 14500),
    TIER43(43, "Tier XLIII", 14850),
    TIER44(44, "Tier XLIV", 15200),
    TIER45(45, "Tier XLV", 15550),
    TIER46(46, "Tier XLVI", 15900),
    TIER47(47, "Tier XLVII", 16250),
    TIER48(48, "Tier XLVIII", 16600),
    TIER49(49, "Tier XLIX", 16950),
    TIER50(50, "Tier L", 17300),
    TIER51(51, "Tier LI", 17650),
    TIER52(52, "Tier LII", 18000),
    TIER53(53, "Tier LIII", 18350),
    TIER54(54, "Tier LIV", 18700),
    TIER55(55, "Tier LV", 19050),
    TIER56(56, "Tier LVI", 19400),
    TIER57(57, "Tier LVII", 19750),
    TIER58(58, "Tier LVIII", 20100),
    TIER59(59, "Tier LIX", 20450),
    TIER60(60, "Tier LX", 20800),
    TIER61(61, "Tier LXI", 21150),
    TIER62(62, "Tier LXII", 21500),
    TIER63(63, "Tier LXIII", 21850),
    TIER64(64, "Tier LXIV", 22200),
    TIER65(65, "Tier LXV", 22550),
    TIER66(66, "Tier LXVI", 22900),
    TIER67(67, "Tier LXVII", 23250),
    TIER68(68, "Tier LXVIII", 23600),
    TIER69(69, "Tier LXIX", 23950),
    TIER70(70, "Tier LXX", 24300),
    TIER71(71, "Tier LXXI", 24650),
    TIER72(72, "Tier LXXII", 25000),
    TIER73(73, "Tier LXXIII", 25350),
    TIER74(74, "Tier LXXIV", 25700),
    TIER75(75, "Tier LXXV", 26050),
    TIER76(76, "Tier LXXVI", 26400),
    TIER77(77, "Tier LXXVII", 26750),
    TIER78(78, "Tier LXXVIII", 27100),
    TIER79(79, "Tier LXXIX", 27450),
    TIER80(80, "Tier LXXX", 27800),
    TIER81(81, "Tier LXXXI", 28150),
    TIER82(82, "Tier LXXXII", 28500),
    TIER83(83, "Tier LXXXIII", 28850),
    TIER84(84, "Tier LXXXIV", 29200),
    TIER85(85, "Tier LXXXV", 29550),
    TIER86(86, "Tier LXXXVI", 29900),
    TIER87(87, "Tier LXXXVII", 30250),
    TIER88(88, "Tier LXXXVIII", 30600),
    TIER89(89, "Tier LXXXIX", 30950),
    TIER90(90, "Tier XC", 31300),
    TIER91(91, "Tier XCI", 31650),
    TIER92(92, "Tier XCII", 32000),
    TIER93(93, "Tier XCIII", 32350),
    TIER94(94, "Tier XCIV", 32700),
    TIER95(95, "Tier XCV", 33050),
    TIER96(96, "Tier XCVI", 33400),
    TIER97(97, "Tier XCVII", 33750),
    TIER98(98, "Tier XCVIII", 34100),
    TIER99(99, "Tier XCIX", 34450),
    TIER100(100, "Tier C", 34800),

    ;

    private final int id;
    private final String name;
    private final int durability;

    Tiers(int id, String name, int durability) {
        this.id = id;
        this.name = name;
        this.durability = durability;
    }

    Tiers(int id, String name){
        this.id = id;
        this.name = name;
        this.durability = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDurability() {
        return durability;
    }
}
