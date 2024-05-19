package com.github.utilcubed.autoinventory;

import com.github.utilcubed.autoinventory.ActionableItems.*;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.function.Function;

public class Player {

    private boolean wasAttacking = false;
    private boolean wasUsing = false;
    private boolean wasOpeningContainer = false;
    private boolean wasFlying = false;

    private final ActionableItem[] mainHandItems = {
            new Bow(),
            // new Firework(),
            new MainTool(),
            // new Pearl(),
            // new Scalffolding(),
            new SecondaryTool(),
            // new Torch()
            new EnderChest()
    };
    private final ActionableItem[] offHandItems = {
            new Food(),
            new Shield()
    };

    public void onTick(MinecraftClient client) {
        if (client.player != null) {
            int selectedSlot = client.player.getInventory().selectedSlot;

            // Update player
            update(client, selectedSlot);

            // If event is happening
            if (client.options.attackKey.isPressed()) {
                if (!wasAttacking) {
                    attackStart(client, selectedSlot);
                }
            }
            if (client.options.useKey.isPressed()) {
                if (!wasUsing) {
                    useStart(client, selectedSlot);
                }
            }
            if (AutoInventoryClient.openContainerKey.isPressed()) {
                if (!wasOpeningContainer) {
                    openContainerStart(client, selectedSlot);
                }
            }
            if (client.player.isFallFlying()) {
                if (!wasFlying) {
                    flyStart(client, selectedSlot);
                }
            }

            // If key stopped
            if (!client.options.attackKey.isPressed() && wasAttacking) {
                attackEnd(client, selectedSlot);
            }
            if (!client.options.useKey.isPressed() && wasUsing) {
                useEnd(client, selectedSlot);
            }
            if (!AutoInventoryClient.openContainerKey.isPressed() && wasOpeningContainer) {
                openContainerEnd(client, selectedSlot);
            }
            if (!client.player.isFallFlying() && wasFlying) {
                flyEnd(client, selectedSlot);
            }

            // Update key states
            wasAttacking = client.options.attackKey.isPressed();
            wasUsing = client.options.useKey.isPressed();
            wasOpeningContainer = AutoInventoryClient.openContainerKey.isPressed();
            wasFlying = client.player.isFallFlying();

        }
    }

    public void update(MinecraftClient client, int selectedSlot) {
        assert client.player != null;
        forEach(mainHandItems, item -> item.update(client, selectedSlot));
        forEach(offHandItems, item -> item.update(client, selectedSlot));
    }

    public void useStart(MinecraftClient client, int selectedSlot) {
        assert client.player != null;
        forEach(mainHandItems, item -> item.useStart(client, selectedSlot));
        forEach(offHandItems, item -> item.useStart(client, selectedSlot));
    }

    public void useEnd(MinecraftClient client, int selectedSlot) {
        assert client.player != null;
        forEach(mainHandItems, item -> item.useEnd(client, selectedSlot));
        forEach(offHandItems, item -> item.useEnd(client, selectedSlot));
    }

    public void attackStart(MinecraftClient client, int selectedSlot) {
        assert client.player != null;
        forEach(mainHandItems, item -> item.attackStart(client, selectedSlot));
        forEach(offHandItems, item -> item.attackStart(client, selectedSlot));
    }

    public void attackEnd(MinecraftClient client, int selectedSlot) {
        assert client.player != null;
        forEach(mainHandItems, item -> item.attackEnd(client, selectedSlot));
        forEach(offHandItems, item -> item.attackEnd(client, selectedSlot));
    }

    public void openContainerStart(MinecraftClient client, int selectedSlot) {
        assert client.player != null;
        forEach(mainHandItems, item -> item.openContainerStart(client, selectedSlot));
        forEach(offHandItems, item -> item.openContainerStart(client, selectedSlot));
    }

    public void openContainerEnd(MinecraftClient client, int selectedSlot) {
        assert client.player != null;
        forEach(mainHandItems, item -> item.openContainerEnd(client, selectedSlot));
        forEach(offHandItems, item -> item.openContainerEnd(client, selectedSlot));
    }

    public void flyStart(MinecraftClient client, int selectedSlot) {
        assert client.player != null;
        forEach(mainHandItems, item -> item.flyStart(client, selectedSlot));
        forEach(offHandItems, item -> item.flyStart(client, selectedSlot));
    }

    public void flyEnd(MinecraftClient client, int selectedSlot) {
        assert client.player != null;
        forEach(mainHandItems, item -> item.flyEnd(client, selectedSlot));
        forEach(offHandItems, item -> item.flyEnd(client, selectedSlot));
    }


    private void forEach(ActionableItem[] items, Function<ActionableItem, Boolean> action) {
        for (ActionableItem item : items) {
            if (action.apply(item)) {
                return;
            }
        }
    }


    public static boolean shouldPreventAttack(MinecraftClient client, ClientPlayerEntity player) {
        // If player is holding sword
        if (player != null && player.getMainHandStack().getItem() instanceof SwordItem) {
            HitResult hitResult = client.crosshairTarget;

            // If hit is nothing, prevent
            if (hitResult == null) {
                return true;
            }
            // If hit is entity, not prevent
            else if (hitResult.getType() == HitResult.Type.ENTITY) {
                return false;
            }
            // If hit is cobweb, not prevent
            else if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                assert client.world != null;
                return client.world.getBlockState(blockHitResult.getBlockPos()).getBlock() != Blocks.COBWEB;
            }
            // Else prevent
            return true;
        }
        // Else allow attack
        return false;
    }
}
