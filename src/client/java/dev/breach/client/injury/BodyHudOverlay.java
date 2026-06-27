package dev.breach.client.injury;

import dev.breach.gameplay.injury.BodyPart;
import dev.breach.gameplay.injury.InjuryConstants;
import dev.breach.gameplay.injury.InjuryData;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class BodyHudOverlay {
	private static InjuryData clientData = InjuryData.createDefault();

	private BodyHudOverlay() {
	}

	public static void update(InjuryData data) {
		clientData = data;
	}

	public static void render(GuiGraphicsExtractor graphics) {
		int screenWidth = graphics.guiWidth();
		int screenHeight = graphics.guiHeight();
		int baseX = screenWidth - 70;
		int baseY = screenHeight - 95;
		int colorBox = 0xAA000000;

		graphics.fill(baseX - 4, baseY - 4, baseX + 56, baseY + 76, colorBox);

		drawPart(graphics, baseX + 16, baseY, 12, 12, BodyPart.HEAD);
		drawPart(graphics, baseX + 12, baseY + 14, 20, 18, BodyPart.CHEST);
		drawPart(graphics, baseX, baseY + 16, 10, 16, BodyPart.LEFT_ARM);
		drawPart(graphics, baseX + 34, baseY + 16, 10, 16, BodyPart.RIGHT_ARM);
		drawPart(graphics, baseX + 12, baseY + 36, 9, 18, BodyPart.LEFT_LEG);
		drawPart(graphics, baseX + 23, baseY + 36, 9, 18, BodyPart.RIGHT_LEG);
	}

	private static void drawPart(GuiGraphicsExtractor graphics, int x, int y, int w, int h, BodyPart part) {
		graphics.fill(x, y, x + w, y + h, colorFor(clientData.get(part)));
	}

	private static int colorFor(int health) {
		if (health >= InjuryConstants.MAX_PART_HEALTH) {
			return 0xFF8A9099;
		}
		float ratio = health / (float) InjuryConstants.MAX_PART_HEALTH;
		if (ratio > 0.66f) {
			return 0xFFEAD64D;
		}
		if (ratio > 0.33f) {
			return 0xFFE8903A;
		}
		return 0xFFE04B4B;
	}
}
