package dev.breach.client.injury;

import dev.breach.BreachMod;
import dev.breach.gameplay.injury.BodyPart;
import dev.breach.gameplay.injury.InjuryConstants;
import net.minecraft.resources.Identifier;

/**
 * Atlas layout for {@code textures/gui/body_hud.png} and {@code body_hud_template.png}.
 * <p>
 * 128x128 PNG, 6 columns x 4 rows, 20x20 px cells with 1 px gutters.
 * Columns: HEAD, CHEST, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG.
 * Rows: HEALTHY (gray), CAUTION (yellow), WOUNDED (orange), CRITICAL (red).
 */
public final class BodyHudAtlas {
	public static final Identifier TEXTURE = BreachMod.id("textures/gui/body_hud.png");
	public static final int TEX_SIZE = 128;
	public static final int CELL = 20;
	public static final int GUTTER = 1;
	public static final int COLS = 6;
	public static final int ROWS = 4;

	public enum HealthBand {
		HEALTHY(0),
		CAUTION(1),
		WOUNDED(2),
		CRITICAL(3);

		private final int row;

		HealthBand(int row) {
			this.row = row;
		}

		public int row() {
			return row;
		}

		public static HealthBand forHealth(int health) {
			if (health >= InjuryConstants.MAX_PART_HEALTH) {
				return HEALTHY;
			}
			float ratio = health / (float) InjuryConstants.MAX_PART_HEALTH;
			if (ratio > 0.66f) {
				return CAUTION;
			}
			if (ratio > 0.33f) {
				return WOUNDED;
			}
			return CRITICAL;
		}
	}

	private BodyHudAtlas() {
	}

	public static int u(BodyPart part) {
		return part.index() * (CELL + GUTTER) + GUTTER;
	}

	public static int v(HealthBand band) {
		return band.row() * (CELL + GUTTER) + GUTTER;
	}
}
