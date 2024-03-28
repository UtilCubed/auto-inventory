package com.github.utilcubed.autoinventory;

import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.dynamic.Range;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Player {
    private int weaponSlot = 0;
    private int toolSlot = 1;
    private int firstUtilitySlot = 7;
    private int secondUtilitySlot = 8;
    private int enderChestSlot = -1;

    private int openContainer_phase = 0;
    private int openContainer_y = 0;

    public void update(MinecraftClient client) {
        if (openContainer_phase > 0) {
            openContainerUpdate(client);
        }
    }

    // Left click
    public void attack(MinecraftClient client) {

    }

    // Right click
    public void use(MinecraftClient client) {

    }

    // Open container key
    public void openContainer(MinecraftClient client) {

        if (client.player == null || client.player.getInventory() == null)
            return;

        int enderChestSlot = getItemSlot(client, Items.ENDER_CHEST);

        // If no ender chest, end
        if (enderChestSlot == -1)
            return;
        this.enderChestSlot = enderChestSlot;
        swap(client, enderChestSlot, secondUtilitySlot);
        client.player.getInventory().selectedSlot = secondUtilitySlot;

        // If not on ground, end
        if (client.player.isOnGround() != true)
            return;
        client.player.setPitch(90.0f);
        client.player.jump();

        openContainer_phase = 5;
        openContainer_y = client.player.getBlockPos().getY();
    }

    public void openContainerUpdate(MinecraftClient client) {
        switch (openContainer_phase) {
            case 5:
                // Swap silk touch pickaxe to toolSlot
                openContainer_phase--;
                int silkTouchSlot = getItemSlot(client, Items.DIAMOND_PICKAXE);
                if (silkTouchSlot == -1)
                    return;
                swap(client, silkTouchSlot, toolSlot);
            case 4:
                // If there is block below player
                if (client.player.getBlockPos().getY() <= openContainer_y)
                    return;
                openContainer_phase--;
                interact(client, Hand.MAIN_HAND, 3);
                break;
            case 3:
                // Swap ender chest back
                openContainer_phase--;
                swap(client, enderChestSlot, secondUtilitySlot);
                break;
            case 2:
                // Select toolSlot
                openContainer_phase--;
                client.player.getInventory().selectedSlot = toolSlot;
            case 1:
                // Open ender chest
                if (targetedBlockState(client, 3).getBlock() != Blocks.ENDER_CHEST)
                    return;
                openContainer_phase--;
                interact(client, Hand.MAIN_HAND, 3);
                break;
            default:
                openContainer_phase--;
                break;
        }
    }

    public int getItemSlot(MinecraftClient client, Item item) {
        int slot = -1;
        PlayerInventory inventory = client.player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).getItem() == item) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    public void swap(MinecraftClient client, int from, int to) {
        ClientPlayerInteractionManager interactionManager = client.interactionManager;

        if (interactionManager != null && client.player != null && client.player.getInventory() != null) {
            interactionManager.clickSlot(client.player.playerScreenHandler.syncId, from, to, SlotActionType.SWAP,
                    client.player);
        }
    }

    public void interact(MinecraftClient client, Hand hand, int range) {
        HitResult hitResult = client.crosshairTarget;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK)
            return;
        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        Vec3d hitPos = blockHitResult.getPos();
        Vec3d playerPos = client.player.getPos();
        double distance = hitPos.distanceTo(playerPos);
        // Check it is within range
        if (range != -1 && distance > range)
            return;

        client.interactionManager.interactBlock(client.player, hand, blockHitResult);
    }

    public void place(MinecraftClient client, Hand hand, int range) {

        // HitResult hitResult = client.crosshairTarget;
        // if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK)
        // return;
        // BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        // Vec3d hitPos = blockHitResult.getPos();
        // Vec3d playerPos = client.player.getPos();
        // double distance = hitPos.distanceTo(playerPos);
        // // Check it is within range
        // if (range != -1 && distance > range)
        // return;
        // // Go 1 unit closer to the player
        // if (distance >= 1.0) {
        // Vec3d direction = playerPos.subtract(hitPos).normalize();
        // blockHitResult = new BlockHitResult(hitPos.add(direction),
        // blockHitResult.getSide(),
        // blockHitResult.getBlockPos().add(0, 1, 0), // TERRIBLE but i honestly give up
        // blockHitResult.isInsideBlock());
        // }

        // if (client.interactionManager.interactBlock(client.player, hand,
        // blockHitResult) == ActionResult.FAIL) {
        // blockHitResult = new BlockHitResult(client.player.getPos(), Direction.UP,
        // client.player.getBlockPos().down(), false);
        // client.interactionManager.interactBlock(client.player, hand, blockHitResult);
        // }
    }

    public BlockState targetedBlockState(MinecraftClient client, int range) {
        HitResult hitResult = client.crosshairTarget;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK)
            return null;
        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        // Check it is within range
        if (range != -1) {
            Vec3d hitPos = blockHitResult.getPos();
            Vec3d playerPos = client.player.getPos();
            double distance = hitPos.distanceTo(playerPos);
            if (distance > range)
                return null;
        }
        return client.world.getBlockState(blockHitResult.getBlockPos());
    }
}
