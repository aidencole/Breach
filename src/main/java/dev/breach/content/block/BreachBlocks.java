package dev.breach.content.block;

import dev.breach.BreachMod;
import dev.breach.gameplay.medical.MedicalBedBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public final class BreachBlocks {
	public static final Block CHALLENGE_REVIVE = register(
			"challenge_revive",
			ChallengeReviveBlock::new,
			BlockBehaviour.Properties.of()
					.mapColor(DyeColor.RED)
					.strength(2.0f)
					.sound(SoundType.STONE)
	);

	public static final MedicalBedBlock MEDICAL_BED = register(
			"medical_bed",
			MedicalBedBlock::new,
			BlockBehaviour.Properties.of()
					.mapColor(DyeColor.RED)
					.strength(0.2f)
					.sound(SoundType.WOOD)
					.noOcclusion()
	);

	private BreachBlocks() {
	}

	public static void register() {
		BreachMod.LOGGER.info("Registered Breach blocks");
	}

	private static <T extends Block> T register(String path, Function<BlockBehaviour.Properties, T> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, BreachMod.id(path));
		T block = factory.apply(properties.setId(blockKey));
		Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, BreachMod.id(path));
		BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
		Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
		return block;
	}
}
