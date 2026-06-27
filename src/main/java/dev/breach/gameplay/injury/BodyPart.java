package dev.breach.gameplay.injury;

public enum BodyPart {
	HEAD(0),
	CHEST(1),
	LEFT_ARM(2),
	RIGHT_ARM(3),
	LEFT_LEG(4),
	RIGHT_LEG(5);

	private final int index;

	BodyPart(int index) {
		this.index = index;
	}

	public int index() {
		return index;
	}

	public static BodyPart fromIndex(int index) {
		return values()[index];
	}
}
