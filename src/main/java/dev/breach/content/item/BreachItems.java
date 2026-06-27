package dev.breach.content.item;

import dev.breach.BreachMod;
import dev.breach.gameplay.medical.MedkitItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public final class BreachItems {
	public static final Item MEDKIT = register(
			"medkit",
			MedkitItem::new,
			new Item.Properties().stacksTo(16)
	);

	private BreachItems() {
	}

	public static void register() {
		BreachMod.LOGGER.info("Registered Breach items");
	}

	private static Item register(String path, Function<Item.Properties, Item> factory, Item.Properties properties) {
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, BreachMod.id(path));
		Item item = factory.apply(properties.setId(itemKey));
		return Registry.register(BuiltInRegistries.ITEM, itemKey, item);
	}
}
