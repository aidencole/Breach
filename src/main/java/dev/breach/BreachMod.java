package dev.breach;

import dev.breach.content.block.BreachBlocks;
import dev.breach.content.dimension.BreachDimensions;
import dev.breach.content.entity.BreachEntities;
import dev.breach.content.item.BreachItems;
import dev.breach.core.event.EventSessionManager;
import dev.breach.core.network.BreachNetworking;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BreachMod implements ModInitializer {
	public static final String MOD_ID = "breach";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Breach — Sculk Dimension event mod");

		BreachNetworking.registerPayloads();
		BreachBlocks.register();
		BreachItems.register();
		BreachEntities.register();
		BreachDimensions.register();
		EventSessionManager.init();

		LOGGER.info("Breach core systems registered");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
