package dev.breach.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.breach.BreachFeatures;
import dev.breach.content.block.BreachBlocks;
import dev.breach.content.item.BreachItems;
import dev.breach.gameplay.downed.DownedController;
import dev.breach.gameplay.injury.BodyPart;
import dev.breach.gameplay.injury.InjuryAttachment;
import dev.breach.gameplay.injury.InjuryManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;

public final class BreachCommands {
	private BreachCommands() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("breach")
				.requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)))
				.then(Commands.literal("hurt")
						.then(Commands.argument("part", StringArgumentType.word())
								.then(Commands.argument("amount", IntegerArgumentType.integer(1, 6))
										.executes(ctx -> {
											if (!BreachFeatures.INJURY_SYSTEM_ENABLED) {
												ctx.getSource().sendFailure(Component.literal("Injury system is disabled."));
												return 0;
											}
											ServerPlayer player = ctx.getSource().getPlayerOrException();
											BodyPart part = BodyPart.valueOf(StringArgumentType.getString(ctx, "part").toUpperCase());
											int amount = IntegerArgumentType.getInteger(ctx, "amount");
											InjuryManager.damage(player, part, amount);
											ctx.getSource().sendSuccess(() -> Component.literal("Damaged " + part.name() + " by " + amount), true);
											return 1;
										}))))
				.then(Commands.literal("heal")
						.executes(ctx -> {
							if (!BreachFeatures.INJURY_SYSTEM_ENABLED) {
								ctx.getSource().sendFailure(Component.literal("Injury system is disabled."));
								return 0;
							}
							ServerPlayer player = ctx.getSource().getPlayerOrException();
							InjuryAttachment.get(player).fullHeal();
							InjuryManager.sync(player);
							ctx.getSource().sendSuccess(() -> Component.literal("Fully healed injuries."), true);
							return 1;
						}))
				.then(Commands.literal("kit")
						.executes(ctx -> {
							ServerPlayer player = ctx.getSource().getPlayerOrException();
							player.getInventory().add(new net.minecraft.world.item.ItemStack(BreachItems.MEDKIT, 4));
							player.getInventory().add(new net.minecraft.world.item.ItemStack(BreachBlocks.MEDICAL_BED, 1));
							ctx.getSource().sendSuccess(() -> Component.literal("Added medkits and a medical bed."), true);
							return 1;
						}))
				.then(Commands.literal("down")
						.executes(ctx -> {
							if (!BreachFeatures.DOWNED_SYSTEM_ENABLED) {
								ctx.getSource().sendFailure(Component.literal("Downed system is disabled."));
								return 0;
							}
							ServerPlayer player = ctx.getSource().getPlayerOrException();
							DownedController.downPlayer(player);
							ctx.getSource().sendSuccess(() -> Component.literal("Triggered downed state."), true);
							return 1;
						}))
				.then(Commands.literal("reset")
						.executes(ctx -> {
							ServerPlayer player = ctx.getSource().getPlayerOrException();
							DownedController.clearDowned(player);
							ctx.getSource().sendSuccess(() -> Component.literal("Cleared downed state."), true);
							return 1;
						})));
	}
}
