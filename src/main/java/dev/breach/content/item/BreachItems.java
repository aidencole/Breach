package dev.breach.content.item;

import dev.breach.BreachMod;
import dev.breach.gameplay.downed.DownedManager;
import dev.breach.gameplay.downed.FallenBodyEntity;
import dev.breach.gameplay.injury.InjuryAttachment;
import dev.breach.gameplay.injury.InjuryManager;
import dev.breach.gameplay.medical.MedkitItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public final class BreachItems {
	public static final Item MEDKIT = register("medkit", new MedkitItem(new Item.Properties().stacksTo(16)));

	private BreachItems() {
	}

	public static void register() {
		BreachMod.LOGGER.info("Registered Breach items");
	}

	private static Item register(String path, Item item) {
		return Registry.register(BuiltInRegistries.ITEM, BreachMod.id(path), item);
	}
}
