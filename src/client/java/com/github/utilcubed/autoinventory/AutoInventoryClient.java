package com.github.utilcubed.autoinventory;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class AutoInventoryClient implements ClientModInitializer {
	// Own classes
	private static Player player = new Player();

	// Keybindings
	public static final KeyBinding openContainerKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.autoinventory.opencontainer", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z,
			"category.autoinventory.keybindings"));

	public boolean wasAttacking = false;
	public boolean wasUsing = false;
	public boolean wasOpeningContainer = false;
	public boolean wasFlying = false;

	@Override
	public void onInitializeClient() {

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (client.player != null) {

				// Update player
				player.update(client);

				// If event is happening
				if (client.options.attackKey.isPressed()) {
					player.whileAttack(client);
					if (!wasAttacking) {
						player.startAttack(client);
					}
				}
				if (client.options.useKey.isPressed()) {
					player.whileUse(client);
					if (!wasUsing) {
						player.startUse(client);
					}
				}
				if (openContainerKey.isPressed()) {
					player.whileOpenContainer(client);
					if (!wasOpeningContainer) {
						player.startOpenContainer(client);
					}
				}
				if (client.player.isFallFlying()) {
					player.whileFlying(client);
					if (!wasFlying) {
						player.startFlying(client);
					}
				}

				// If key stopped
				if (!client.options.attackKey.isPressed() && wasAttacking) {
					player.endAttack(client);
				}
				if (!client.options.useKey.isPressed() && wasUsing) {
					player.endUse(client);
				}
				if (!openContainerKey.isPressed() && wasOpeningContainer) {
					player.endOpenContainer(client);
				}
				if (!client.player.isFallFlying() && wasFlying) {
					player.endFlying(client);
				}

				// Update key states
				wasAttacking = client.options.attackKey.isPressed();
				wasUsing = client.options.useKey.isPressed();
				wasOpeningContainer = openContainerKey.isPressed();
				wasFlying = client.player.isFallFlying();
			}

		});

	}
}