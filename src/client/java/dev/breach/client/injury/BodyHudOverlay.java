package dev.breach.client.injury;

import dev.breach.gameplay.injury.BodyPart;
import dev.breach.gameplay.injury.InjuryData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;

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
		int baseX = screenWidth - 58;
		int baseY = screenHeight - 88;

		graphics.fill(baseX - 6, baseY - 6, baseX + 50, baseY + 72, 0xAA101010);

		drawPart(graphics, baseX + 14, baseY, BodyPart.HEAD);
		drawPart(graphics, baseX + 10, baseY + 13, BodyPart.CHEST);
		drawPart(graphics, baseX, baseY + 15, BodyPart.LEFT_ARM);
		drawPart(graphics, baseX + 30, baseY + 15, BodyPart.RIGHT_ARM);
		drawPart(graphics, baseX + 10, baseY + 34, BodyPart.LEFT_LEG);
		drawPart(graphics, baseX + 22, baseY + 34, BodyPart.RIGHT_LEG);
	}

	private static void drawPart(GuiGraphicsExtractor graphics, int x, int y, BodyPart part) {
		int health = clientData.get(part);
		BodyHudAtlas.HealthBand band = BodyHudAtlas.HealthBand.forHealth(health);
		int w = part == BodyPart.HEAD ? 12 : part == BodyPart.CHEST ? 20 : part.name().contains("LEG") ? 9 : 10;
		int h = part == BodyPart.HEAD ? 12 : part == BodyPart.CHEST ? 18 : part.name().contains("LEG") ? 18 : 16;

		graphics.blit(
				RenderPipelines.GUI_TEXTURED,
				BodyHudAtlas.TEXTURE,
				x,
				y,
				BodyHudAtlas.u(part),
				BodyHudAtlas.v(band),
				w,
				h,
				BodyHudAtlas.CELL,
				BodyHudAtlas.CELL,
				BodyHudAtlas.TEX_SIZE,
				BodyHudAtlas.TEX_SIZE
		);
	}
}
