package dev.breach.content.entity;

import dev.breach.BreachMod;
import dev.breach.gameplay.downed.FallenBodyEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class BreachEntities {
	public static final EntityType<FallenBodyEntity> FALLEN_BODY = registerFallenBody();

	private BreachEntities() {
	}

	public static void register() {
		FabricDefaultAttributeRegistry.register(FALLEN_BODY, FallenBodyEntity.createAttributes());
		BreachMod.LOGGER.info("Registered Breach entities");
	}

	private static EntityType<FallenBodyEntity> registerFallenBody() {
		Identifier id = BreachMod.id("fallen_body");
		ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
		EntityType<FallenBodyEntity> type = EntityType.Builder.of(FallenBodyEntity::new, MobCategory.MISC)
				.sized(0.6f, 0.2f)
				.clientTrackingRange(64)
				.updateInterval(5)
				.build(key);
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type);
	}
}
