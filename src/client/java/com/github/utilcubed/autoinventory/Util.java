package com.github.utilcubed.autoinventory;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Util {

    public static final int FIRST_SLOT = 0;
    public static final int SECOND_SLOT = 1;
    public static final int THIRD_SLOT = 2;

    public static int getSlotOfItem(MinecraftClient client, Item item) {
        int slot = -1;
        PlayerInventory inventory = client.player.getInventory();
        // First search inventory
        for (int i = 9; i < inventory.size(); i++) {
            if (inventory.getStack(i).getItem() == item) {
                slot = i;
                break;
            }
        }
        // Then check hotbar
        for (int i = 0; i < 9; i++) {
            if (inventory.getStack(i).getItem() == item) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    public static Item getItemInSlot(MinecraftClient client, int slot) {
        PlayerInventory inventory = client.player.getInventory();
        return inventory.getStack(slot).getItem();
    }

    public static int findItemAndSwapTo(MinecraftClient client, Item item, int slot) {
        int itemSlot = getSlotOfItem(client, item);
        if (itemSlot == -1)
            return -1;
        if (itemSlot == client.player.getInventory().selectedSlot)
            return itemSlot;

        swap(client, itemSlot, slot);
        return itemSlot;
    }

    public static void swap(MinecraftClient client, int from, int to) {
        if (from == to)
            return;

        if (from >= 0 && from < 9 && to >= 0 && to < 9) {
            debug(client, "Item is in hotbar");
            assert client.player != null;
            client.player.getInventory().selectedSlot = to;
        }

        ClientPlayerInteractionManager interactionManager = client.interactionManager;

        if (interactionManager != null && client.player != null && client.player.getInventory() != null) {
            interactionManager.clickSlot(client.player.playerScreenHandler.syncId, from, to, SlotActionType.SWAP,
                    client.player);
        } else {
            debug(client, "Failed to swap");
        }
    }

    public static void interact(MinecraftClient client, Hand hand, int range) {
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

    public static BlockState targetedBlockState(MinecraftClient client, int range) {
        BlockHitResult blockHitResult = crosshairBlockHitResult(client, range);
        if (blockHitResult == null)
            return null;
        return client.world.getBlockState(blockHitResult.getBlockPos());
    }

    public static BlockHitResult crosshairBlockHitResult(MinecraftClient client, int range) {
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

    public static int lookAtBlock(MinecraftClient client, BlockPos pos) {
        if (client == null || client.player == null)
            return -1;

        client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
        return 0;
    }

    public static int lookAtBlock(MinecraftClient client, Vec3d pos) {
        if (client == null || client.player == null)
            return -1;

        double dx = Math.floor(pos.getX()) + 0.5;
        double dy = Math.floor(pos.getY()) + 0.5;
        double dz = Math.floor(pos.getZ()) + 0.5;

        client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(dx, dy, dz));
        return 0;
    }

    public static int lookAtBlock(MinecraftClient client, BlockPos pos, Direction direction) {
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

        client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(dx, dy, dz));
        return 0;
    }

    public static void debug(MinecraftClient client, String arg) {
        client.player.sendMessage(Text.literal(arg), false);
    }

    public static void debug(MinecraftClient client, Object arg) {
        client.player.sendMessage(Text.literal(arg.toString()), false);
    }

    public static void debug(MinecraftClient client, int arg) {
        client.player.sendMessage(Text.literal(Integer.toString(arg)), false);
    }
}
