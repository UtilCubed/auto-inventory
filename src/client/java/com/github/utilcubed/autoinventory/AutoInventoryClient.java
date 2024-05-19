package com.github.utilcubed.autoinventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;;

public class AutoInventoryClient implements ClientModInitializer {

    // Keybindings
    public static final KeyBinding openContainerKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autoinventory.opencontainer", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z,
            "category.autoinventory.keybindings"));



    @Override
    public void onInitializeClient() {
        final Player player = new Player();

        ClientTickEvents.START_CLIENT_TICK.register(player::onTick);

        ClientPreAttackCallback listener = new ClientPreAttackCallback() {
            @Override
            public boolean onClientPlayerPreAttack(MinecraftClient client, ClientPlayerEntity player, int clickCount) {
				return Player.shouldPreventAttack(client, player);
            }
        };
        ClientPreAttackCallback.EVENT.register(listener);

    }
}