#!/usr/bin/env python3
"""Generate Breach placeholder textures for HUD atlas and items."""

from pathlib import Path
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/assets/breach"

COLORS = {
    "healthy": (0x8A, 0x90, 0x99, 0xFF),
    "caution": (0xEA, 0xD6, 0x4D, 0xFF),
    "wounded": (0xE8, 0x90, 0x3A, 0xFF),
    "critical": (0xE0, 0x4B, 0x4B, 0xFF),
}

CELL = 20
GUTTER = 1
COLS = 6
ROWS = 4
TEX = 128


def draw_part(draw: ImageDraw.ImageDraw, ox: int, oy: int, part: int, fill):
    outline = tuple(max(0, c - 40) for c in fill[:3]) + (255,)
    if part == 0:  # head
        draw.rectangle((ox + 4, oy + 2, ox + 15, oy + 13), fill=fill, outline=outline)
    elif part == 1:  # chest
        draw.rectangle((ox + 1, oy + 1, ox + 18, oy + 14), fill=fill, outline=outline)
        draw.line((ox + 10, oy + 2, ox + 10, oy + 13), fill=outline, width=1)
    elif part == 2:  # left arm
        draw.rectangle((ox + 5, oy + 1, ox + 14, oy + 18), fill=fill, outline=outline)
    elif part == 3:  # right arm
        draw.rectangle((ox + 5, oy + 1, ox + 14, oy + 18), fill=fill, outline=outline)
    elif part == 4:  # left leg
        draw.rectangle((ox + 6, oy + 1, ox + 13, oy + 18), fill=fill, outline=outline)
    elif part == 5:  # right leg
        draw.rectangle((ox + 6, oy + 1, ox + 13, oy + 18), fill=fill, outline=outline)


def make_body_atlas(template: bool = False) -> Image.Image:
    img = Image.new("RGBA", (TEX, TEX), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    bands = ["healthy", "caution", "wounded", "critical"]
    for row, band in enumerate(bands):
        for col in range(COLS):
            ox = col * (CELL + GUTTER) + GUTTER
            oy = row * (CELL + GUTTER) + GUTTER
            if template:
                draw.rectangle((ox, oy, ox + CELL - 1, oy + CELL - 1), outline=(255, 255, 255, 180))
                draw_part(draw, ox, oy, col, (255, 255, 255, 60))
            else:
                draw_part(draw, ox, oy, col, COLORS[band])
    return img


def make_medkit() -> Image.Image:
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rectangle((2, 4, 13, 13), fill=(240, 240, 240, 255), outline=(120, 120, 120, 255))
    d.rectangle((7, 6, 8, 11), fill=(200, 40, 40, 255))
    d.rectangle((5, 8, 10, 9), fill=(200, 40, 40, 255))
    return img


def make_bed_item() -> Image.Image:
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rectangle((1, 9, 14, 13), fill=(140, 30, 30, 255), outline=(80, 10, 10, 255))
    d.rectangle((1, 6, 14, 9), fill=(220, 220, 230, 255), outline=(160, 160, 170, 255))
    d.rectangle((1, 4, 5, 6), fill=(200, 40, 40, 255))
    return img


def make_revive_block() -> Image.Image:
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rectangle((2, 2, 13, 13), fill=(180, 30, 30, 255), outline=(90, 10, 10, 255))
    d.rectangle((6, 6, 9, 9), fill=(255, 220, 80, 255))
    return img


def main():
    gui = ASSETS / "textures/gui"
    item = ASSETS / "textures/item"
    block = ASSETS / "textures/block"
    for d in (gui, item, block):
        d.mkdir(parents=True, exist_ok=True)

    make_body_atlas(False).save(gui / "body_hud.png")
    make_body_atlas(True).save(gui / "body_hud_template.png")
    make_medkit().save(item / "medkit.png")
    make_bed_item().save(item / "medical_bed.png")
    make_revive_block().save(item / "challenge_revive.png")
    make_revive_block().save(block / "challenge_revive.png")
    print("Generated Breach textures")


if __name__ == "__main__":
    main()
