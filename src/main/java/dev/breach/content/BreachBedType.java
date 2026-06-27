package dev.breach.content;

import net.minecraft.world.item.DyeColor;

/**
 * Custom bed variants for Breach. Uses vanilla bed visuals via {@link DyeColor} for now.
 */
public enum BreachBedType {
	MEDICAL(DyeColor.RED);

	private final DyeColor dyeColor;

	BreachBedType(DyeColor dyeColor) {
		this.dyeColor = dyeColor;
	}

	public DyeColor dyeColor() {
		return dyeColor;
	}
}
