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

	private boolean wasAttacking = false;
	private boolean wasUsing = false;
	private boolean wasOpeningContainer = false;

	@Override
	public void onInitializeClient() {

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null) {

				// Update player
				player.update(client);

				if (client.options.attackKey.isPressed() && !wasAttacking) {
					player.attack(client);
				}
				if (client.options.useKey.isPressed() && !wasUsing) {
					player.use(client);
				}
				if (openContainerKey.isPressed() && !wasOpeningContainer) {
					player.openContainer(client);
				}
			}

			// Update key states
			wasAttacking = client.options.attackKey.isPressed();
			wasUsing = client.options.useKey.isPressed();
			wasOpeningContainer = openContainerKey.isPressed();

		});

	}
}