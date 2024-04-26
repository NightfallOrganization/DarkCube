/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.labymod.emotes;

public enum Emotes {

	STOP_EMOTE(-1),
	BACKFLIP(2),
	DAB(3),
	HELLO(4),
	BOW_THANKS(5),
	HYPE(6),
	TRYINGTOFLY(7),
	INFINITY_SIT(8),
	ZOMBIE(11),
	HULA_HOOP(13),
	CALLING(14),
	FACEPALM(15),
	BRUSH_YOUR_SHOULDERS(18),
	SPLIT(19),
	SALUTE(20),
	BALARINA(22),
	HANDSTAND(31),
	HELICOPTER(32),
	HOLY(33),
	WAVEOVER(34),
	DEEPER_DEEPER(36),
	KARATE(37),
	MOONWALK(38),
	FREEZING(40),
	JUBILATION(41),
	TURTLE(43),
	HEADSPIN(45),
	INFINITY_DAB(46),
	CHICKEN(47),
	THE_FLOSS(49),
	THE_MEGA_THRUST(50),
	THE_CLEANER(51),
	BRIDGE(52),
	MILK_THE_COW(53),
	RURIK(54),
	WAVE(55),
	MONEY_RAIN(57),
	THE_POINTER(59),
	FRIGHTENING(60),
	SAD(61),
	AIR_GUITAR(62),
	WITCH(63),
	LEFT(69),
	RIGHT(70),
	BUUUH(74),
	SPITTING_BARS(75),
	COUNT_MONEY(76),
	HUG(77),
	APPLAUSE(78),
	BOXING(79),
	SHOOT(83),
	THE_POINTING_MAN(84),
	HEART(85),
	NEAR_THE_FALL(86),
	WAITING(89),
	PRAISE_YOUR_ITEM(92),
	LOOK(93),
	I_LOVE_YOU(97),
	SARCASTIC_CLAP(98),
	YOU(101),
	HEAD_ON_WALL(105),
	BALANCE(112),
	LEVELUP(113),
	TAKE_THE_L(114),
	MY_IDOL(121),
	AIRPLANE(122),
	EAGLE(124),
	JOB_WELL_DONE(126),
	ELEPHANT(128),
	PRESENT(130),
	EYES_ON_YOU(131),
	BOW_DOWN(133),
	MANEKI_NEKO(134),
	CONDUCTOR(135),
	DIDI_CHALLENGE(136),
	SNOW_ANGEL(137),
	SNOWBALL(138),
	SPRINKLER(139),
	CALCULATED(140),
	ONE_ARMED_HANDSTAND(141),
	EAT(142),
	SHY(143),
	SIT_UPS(145),
	BREAKDANCE(146),
	MINDBLOW(148),
	FALL(149),
	T_POSE(150),
	JUMPING_JACK(153),
	BACKSTROKE(154),
	ICE_HOCKEY(156),
	LOOK_AT_FIREWORKS(157),
	FINISH_THE_TREE(158),
	ICE_SKATING(159),
	FANCY_FEET(161),
	RONALDO(162),
	TRUE_HEART(163),
	PUMPERNICKEL(164),
	BABY_SHARK(166),
	OPEN_PRESENT(167),
	DJ(170),
	HAVE_TO_PEE(171),
	SNEEZE(173),
	CHEERLEADER(178),
	NARUTO_RUN(180),
	PATI_PATU(181),
	AXE_SWING(182),
	FUSION_LEFT(183),
	FISHING(184),
	FUSION_RIGHT(185),
	BREATHLESS(187),
	GENKIDAMA(189),
	SINGER(191),
	MAGIKARP(192),
	RAGE(193),
	SLAP(194),
	AIR_KISSES(195),
	KNOCKOUT(196),
	MATRIX(197),
	JETPACK(198),
	GOLF(200),
	STADIUM_WAVE(201),
	KICKBOXER(202),
	HANDSHAKE(203),
	CLEANING_THE_FLOOR(204),
	SWEAT_WIPE(209),
	HOKUSPOKUS(213),
	THE_BAT(215),

	;

	private final int id;

	private Emotes(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

}
