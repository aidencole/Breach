package dev.breach.content.block;

import dev.breach.BreachMod;
import dev.breach.gameplay.medical.MedicalBedBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class BreachBlocks {
	public static final Block CHALLENGE_REVIVE = register(
			"challenge_revive",
			new ChallengeReviveBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CONCRETE.pick(DyeColor.RED)))
	);

	public static final MedicalBedBlock MEDICAL_BED = register(
			"medical_bed",
			new MedicalBedBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BED.pick(DyeColor.RED)))
	);

	private BreachBlocks() {
	}

	public static void register() {
		BreachMod.LOGGER.info("Registered Breach blocks");
	}

	private static <T extends Block> T register(String path, T block) {
		Identifier id = BreachMod.id(path);
		Registry.register(BuiltInRegistries.BLOCK, id, block);
		Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, new Item.Properties()));
		return block;
	}
}
