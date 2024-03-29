package com.github.utilcubed.autoinventory;

import java.util.logging.Logger;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents.Command;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.command.argument.EntityAnchorArgumentType.EntityAnchor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.dynamic.Range;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Player {
    private final int weaponSlot = 0;
    private final int toolSlot = 1;
    private final int firstUtilitySlot = 7;
    private final int secondUtilitySlot = 8;
    private int enderChestSlot = -1;
    private int shieldSlot = -1;
    private int bowSlot = -1;
    private int foodSlot = -1;

    private int openContainer_phase = 0;
    private Integer openContainer_y = null;

    private int elytra_landedTickCountdown = 0;

    public void startUpdate(MinecraftClient client) {

        if (openContainer_phase > 0) {
            openContainerUpdate(client);
        }

        if (elytra_landedTickCountdown > 1) {
            elytra_landedTickCountdown--;
        } else if (elytra_landedTickCountdown == 1) {
            if (client == null || client.player == null || client.currentScreen != null)
                return;

            findItemAndSwapTo(client, Items.TORCH, secondUtilitySlot);
            elytra_landedTickCountdown = 0;
        }
    }

    // Left click
    public void startAttack(MinecraftClient client) {

    }

    public void whileAttack(MinecraftClient client) {

    }

    public void endAttack(MinecraftClient client) {

    }

    // Right click
    public void startUse(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null || player.getInventory() == null || client.currentScreen != null)
            return;

        int playerSlot = player.getInventory().selectedSlot;

        // Weapon slot
        if (playerSlot == weaponSlot && getItemInSlot(client, PlayerInventory.OFF_HAND_SLOT) != Items.SHIELD) {
            this.shieldSlot = findItemAndSwapTo(client, Items.SHIELD, PlayerInventory.OFF_HAND_SLOT);
        }
        // Tool slot
        else if (playerSlot == toolSlot && getItemInSlot(client, PlayerInventory.OFF_HAND_SLOT) != Items.BOW) {
            this.bowSlot = findItemAndSwapTo(client, Items.BOW, playerSlot);
        }
        // First utility slot
        else if (playerSlot == firstUtilitySlot) {
            BlockState targetedBlock = targetedBlockState(client, 6);
            // No need to check null

            // If air
            if (targetedBlock == null || targetedBlock.isAir()) {
                client.options.useKey.setPressed(false);
                findItemAndSwapTo(client, Items.ENDER_PEARL, firstUtilitySlot);
            }
            // If scaffold
            else if (targetedBlock.getBlock() == Blocks.SCAFFOLDING) {
                client.options.useKey.setPressed(false);
                findItemAndSwapTo(client, Items.SCAFFOLDING, firstUtilitySlot);
            }

        }
        // Second utility slot
        else if (playerSlot == secondUtilitySlot) {
            BlockState targetedBlock = targetedBlockState(client, 6);
            if (targetedBlock == null)
                return;

            // If solid
            if (targetedBlock != null || !targetedBlock.isAir() && targetedBlock.isSolid()) {
                // If torch
                if (player.getMainHandStack().getItem() == Items.FIREWORK_ROCKET) {
                    client.options.useKey.setPressed(false);
                    findItemAndSwapTo(client, Items.TORCH, secondUtilitySlot);
                }
            }

        }
        // Anything else
        else {
            // If air
            if (targetedBlockState(client, 6) == null || targetedBlockState(client, 6).isAir()) {
                int foodSlot = getSlotOfItem(client, Items.GOLDEN_CARROT);
                if (foodSlot == -1)
                    getSlotOfItem(client, Items.COOKED_BEEF);
                if (foodSlot == -1)
                    getSlotOfItem(client, Items.COOKED_PORKCHOP);
                if (foodSlot == -1)
                    return;
                swap(client, foodSlot, PlayerInventory.OFF_HAND_SLOT);
                this.foodSlot = foodSlot;
            }
        }

    }

    public void whileUse(MinecraftClient client) {

    }

    public void endUse(MinecraftClient client) {
        PlayerEntity player = client.player;

        if (player == null || player.getInventory() == null || client.currentScreen != null)
            return;

        Item itemInOffhand = player.getOffHandStack().getItem();
        Item itemInMainhand = player.getMainHandStack().getItem();

        // Weapon slot
        int playerSlot = player.getInventory().selectedSlot;
        if (playerSlot == weaponSlot && itemInOffhand == Items.SHIELD) {
            if (this.shieldSlot == -1)
                return;

            swap(client, shieldSlot, PlayerInventory.OFF_HAND_SLOT);
            this.shieldSlot = -1;
        }
        // Tool slot
        else if (playerSlot == toolSlot && itemInMainhand == Items.BOW) {
            if (this.bowSlot == -1)
                return;

            swap(client, bowSlot, playerSlot);
            this.bowSlot = -1;
        }
        // Second utility slot
        if (playerSlot == secondUtilitySlot && itemInMainhand == Items.ENDER_CHEST) {

            int slot = this.enderChestSlot;
            if (slot == -1 || slot == playerSlot)
                slot = getSlotOfItem(client, Items.TORCH);
            if (slot == -1) {
                debug(client, "Nowhere to swap ender chest to");
                return;
            }

            swap(client, slot, playerSlot);
        }

        // Offhand is food
        if (itemInOffhand == Items.GOLDEN_CARROT || itemInOffhand == Items.COOKED_BEEF
                || itemInOffhand == Items.COOKED_PORKCHOP) {
            if (this.foodSlot == -1)
                return;

            swap(client, foodSlot, PlayerInventory.OFF_HAND_SLOT);
            this.foodSlot = -1;
        }

    }

    // Flying
    public void startFlying(MinecraftClient client) {
        if (client.player == null || client.player == null || client.currentScreen != null)
            return;

        elytra_landedTickCountdown = 0;
        findItemAndSwapTo(client, Items.FIREWORK_ROCKET, secondUtilitySlot);
    }

    public void whileFlying(MinecraftClient client) {

    }

    public void endFlying(MinecraftClient client) {
        this.elytra_landedTickCountdown = 20;
    }

    // Open container key
    public void startOpenContainer(MinecraftClient client) {
        if (client.player == null || client.player.getInventory() == null)
            return;

        if (client.currentScreen == null) {
            int enderChestSlot = getSlotOfItem(client, Items.ENDER_CHEST);
            int reach = (int) (PlayerEntity.getReachDistance(false) - 0.5);
            BlockState targetedBlock = targetedBlockState(client, reach);
            PlayerEntity player = client.player;

            // If no ender chest, end
            if (enderChestSlot == -1)
                return;
            this.enderChestSlot = enderChestSlot;
            swap(client, enderChestSlot, secondUtilitySlot);
            player.getInventory().selectedSlot = secondUtilitySlot;

            // Check whether you need to jump
            if (targetedBlock == null || !targetedBlock.isSolid()) {
                if (!player.isOnGround())
                    return;
                lookAtBlock(client, player.getBlockPos().down(), Direction.UP);
                player.jump();
                openContainer_y = player.getBlockPos().getY();
            }
            // Edge case, already looking at chest
            else if (targetedBlock.getBlock() == Blocks.ENDER_CHEST) {
                openContainer_phase = 1;
            }
            // Look at center of block
            else {
                BlockHitResult blockHitResult = crosshairBlockHitResult(client, reach);
                if (blockHitResult == null)
                    return;
                lookAtBlock(client, blockHitResult.getBlockPos(), blockHitResult.getSide());
                openContainer_phase = 1;
            }
            openContainer_phase = 5;

        } else {
            // Future code
        }
    }

    public void whileOpenContainer(MinecraftClient client) {

    }

    public void endOpenContainer(MinecraftClient client) {

    }

    public void openContainerUpdate(MinecraftClient client) {
        if (client.player == null || client.player.getInventory() == null || client.currentScreen != null)
            return;

        int reach = (int) PlayerEntity.getReachDistance(false);
        PlayerEntity player = client.player;

        switch (openContainer_phase) {
            case 5:
                // Swap silk touch pickaxe to toolSlot
                openContainer_phase--;
                int silkTouchSlot = getSlotOfItem(client, Items.DIAMOND_PICKAXE);
                if (silkTouchSlot == -1)
                    return;
                swap(client, silkTouchSlot, toolSlot);
                player.setSneaking(true);
                break;
            case 4:
                // If there is block below player or solid target block
                if (openContainer_y != null && player.getBlockPos().getY() <= openContainer_y)
                    return;
                if (targetedBlockState(client, reach) == null || !targetedBlockState(client, reach).isSolid())
                    return;

                openContainer_phase--;
                interact(client, Hand.MAIN_HAND, reach);
                this.openContainer_y = null;
                break;
            case 3:
                // Swap ender chest back
                openContainer_phase--;
                swap(client, enderChestSlot, secondUtilitySlot);
                player.setSneaking(false);
                break;
            case 2:
                // Select toolSlot
                openContainer_phase--;
                player.getInventory().selectedSlot = toolSlot;
            case 1:
                // Open ender chest
                BlockState targetedBlock = targetedBlockState(client, reach);
                if (targetedBlock == null || targetedBlock.getBlock() != Blocks.ENDER_CHEST) {
                    debug(client, "Look at an ender chest");
                    return;
                }
                openContainer_phase--;
                interact(client, Hand.MAIN_HAND, reach);
                break;
            default:
                openContainer_phase--;
                break;
        }
    }

    public void endUpdate(MinecraftClient client) {

    }

    // UTILITY FUNCTIONS

    public int getSlotOfItem(MinecraftClient client, Item item) {
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

    public Item getItemInSlot(MinecraftClient client, int slot) {
        PlayerInventory inventory = client.player.getInventory();
        return inventory.getStack(slot).getItem();
    }

    public int findItemAndSwapTo(MinecraftClient client, Item item, int slot) {
        int itemSlot = getSlotOfItem(client, item);
        if (itemSlot == -1)
            return -1;
        if (itemSlot == client.player.getInventory().selectedSlot)
            return itemSlot;

        swap(client, itemSlot, slot);
        return itemSlot;
    }

    public void swap(MinecraftClient client, int from, int to) {
        if (from == to)
            return;

        ClientPlayerInteractionManager interactionManager = client.interactionManager;

        if (interactionManager != null && client.player != null && client.player.getInventory() != null) {
            interactionManager.clickSlot(client.player.playerScreenHandler.syncId, from, to, SlotActionType.SWAP,
                    client.player);
        } else {
            debug(client, "Failed to swap");
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

    public BlockState targetedBlockState(MinecraftClient client, int range) {
        BlockHitResult blockHitResult = crosshairBlockHitResult(client, range);
        if (blockHitResult == null)
            return null;
        return client.world.getBlockState(blockHitResult.getBlockPos());
    }

    public BlockHitResult crosshairBlockHitResult(MinecraftClient client, int range) {
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
        return blockHitResult;
    }

    private static int lookAtBlock(MinecraftClient client, BlockPos pos) {
        if (client == null || client.player == null)
            return -1;

        client.player.lookAt(EntityAnchor.EYES, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
        return 0;
    }

    private static int lookAtBlock(MinecraftClient client, Vec3d pos) {
        if (client == null || client.player == null)
            return -1;

        double dx = Math.floor(pos.getX()) + 0.5;
        double dy = Math.floor(pos.getY()) + 0.5;
        double dz = Math.floor(pos.getZ()) + 0.5;

        client.player.lookAt(EntityAnchor.EYES, new Vec3d(dx, dy, dz));
        return 0;
    }

    private static int lookAtBlock(MinecraftClient client, BlockPos pos, Direction direction) {
        if (client == null || client.player == null)
            return -1;

        double dx = pos.getX() + 0.5;
        double dy = pos.getY() + 0.5;
        double dz = pos.getZ() + 0.5;

        switch (direction) {
            case NORTH:
                dz -= 0.5;
                break;
            case SOUTH:
                dz += 0.5;
                break;
            case EAST:
                dx += 0.5;
                break;
            case WEST:
                dx -= 0.5;
                break;
            case UP:
                dy += 0.5;
                break;
            case DOWN:
                dy -= 0.5;
                break;
            default:
                break;
        }

        client.player.lookAt(EntityAnchor.EYES, new Vec3d(dx, dy, dz));
        return 0;
    }

    public static void debug(MinecraftClient client, Object arg) {
        client.player.sendMessage(Text.of(arg.toString()), false);
    }

    public static void debug(MinecraftClient client, int arg) {
        client.player.sendMessage(Text.of(Integer.toString(arg)), false);

    }

}
