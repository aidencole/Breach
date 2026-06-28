Fallen body GeckoLib model (GeckoLib 5.x)

Place your Blockbench export here:

  fallen_body_wide.geo.json

Full path in this project:

  src/main/resources/assets/breach/geckolib/models/entity/fallen_body_wide.geo.json

Blockbench setup:
  - Format: GeckoLib Animated Model
  - Mod ID: breach
  - Object ID: fallen_body_wide
  - Pose the model lying on the ground in Blockbench (the mod does not rotate limbs)
  - Put the root bone origin at ground level
  - Default facing in Blockbench should match Minecraft south (-Z)

The bundled file is a flat placeholder. Replace it with your custom model.

Test commands (OP):
  /breach model   spawns the geo model 1.5 blocks ahead of you
  /breach body    spawns at your feet

Both only set position + yaw. All pose/orientation comes from your geo file.

Install GeckoLib 5.5.3+ for Minecraft 26.2 when testing outside the dev environment.
