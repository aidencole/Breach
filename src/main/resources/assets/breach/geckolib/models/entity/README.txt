Blockbench GeckoLib exports for fallen bodies.

Required files:
  fallen_body_wide.geo.json
  fallen_body_slim.geo.json

Full path:
  src/main/resources/assets/breach/geckolib/models/entity/

Blockbench export:
  - Format: GeckoLib Animated Model
  - Mod ID: breach
  - Object ID: fallen_body_wide  (or fallen_body_slim for the slim file)

The mod picks wide vs slim automatically from the owner's Minecraft skin model.

Outer layers (jacket, hat, sleeves, pants):
  - Add overlay bones to your geo file using standard player outer UVs
  - Name them with _outer suffix, outer_ prefix, or vanilla names:
      hat, jacket, left_sleeve, right_sleeve, left_pants, right_pants
  - The mod renders those bones in a second inflated pass with the same skin texture

Inner/base bones render on the first pass. Pose comes entirely from your geo files.

Test: /breach model
