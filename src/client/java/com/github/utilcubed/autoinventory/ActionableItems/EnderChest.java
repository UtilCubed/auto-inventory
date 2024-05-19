package com.github.utilcubed.autoinventory.ActionableItems;

import com.github.utilcubed.autoinventory.Util;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class EnderChest implements ActionableItem {

    private int enderChestSlot = -1;
    private int openContainer_phase = 0;
    private Integer openContainer_y = null;

    @Override
    public boolean update(MinecraftClient client, int selectedSlot) {
        if (openContainer_phase > 0) {
            if (client.player == null || client.player.getInventory() == null || client.currentScreen != null)
                return false;

            int reach = (int) PlayerEntity.getReachDistance(false);
            PlayerEntity player = client.player;

            switch (openContainer_phase) {
                case 5:
                    // Swap silk touch pickaxe to toolSlot
                    openContainer_phase--;
//                    int silkTouchSlot = Util.getSlotOfItem(client, Items.DIAMOND_PICKAXE);
//                    if (silkTouchSlot == -1)
//                        return false;
//                    Util.swap(client, silkTouchSlot, toolSlot);
                    player.setSneaking(true);
                    break;
                case 4:
                    // If there is block below player or solid target block
                    if (openContainer_y != null && player.getBlockPos().getY() <= openContainer_y)
                        return false;
                    if (Util.targetedBlockState(client, reach) == null || !Util.targetedBlockState(client, reach).isSolid())
                        return false;

                    openContainer_phase--;
                    Util.interact(client, Hand.MAIN_HAND, reach);
                    this.openContainer_y = null;
                    break;
                case 3:
                    // Swap ender chest back
                    openContainer_phase--;
                    Util.swap(client, enderChestSlot, Util.THIRD_SLOT);
                    player.setSneaking(false);
                    break;
                case 2:
                    // Select toolSlot
                    openContainer_phase--;
                    player.getInventory().selectedSlot = Util.SECOND_SLOT;
                case 1:
                    // Open ender chest
                    BlockState targetedBlock = Util.targetedBlockState(client, reach);
                    if (targetedBlock == null || targetedBlock.getBlock() != Blocks.ENDER_CHEST) {
                        Util.debug(client, "Look at an ender chest");
                        return false;
                    }
                    openContainer_phase--;
                    Util.interact(client, Hand.MAIN_HAND, reach);
                    break;
                default:
                    openContainer_phase--;
                    break;
            }

            return false;
        }

        return false;
    }

    @Override
    public boolean useStart(MinecraftClient client, int selectedSlot) {
        return false;
    }

    @Override
    public boolean useEnd(MinecraftClient client, int selectedSlot) {
        return false;
    }

    @Override
    public boolean attackStart(MinecraftClient client, int selectedSlot) {
        return false;
    }

    @Override
    public boolean attackEnd(MinecraftClient client, int selectedSlot) {
        return false;
    }

    @Override
    public boolean openContainerStart(MinecraftClient client, int selectedSlot) {

        if (client.player == null || client.player.getInventory() == null)
            return false;

        BlockPos nearbyEnderChest = findNearbyEnderChest(client.player, client.world);

        if (nearbyEnderChest != null) {
            Util.debug(client, "Using nearby ender chest");
            Util.lookAtBlock(client, nearbyEnderChest, Direction.UP);
            BlockHitResult blockHitResult = new BlockHitResult(nearbyEnderChest.toCenterPos(), Direction.UP, nearbyEnderChest, false);
            assert client.interactionManager != null;
            client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, blockHitResult);
            return true;
        }

        if (client.currentScreen == null) {
            int enderChestSlot = Util.getSlotOfItem(client, Items.ENDER_CHEST);
            int reach = (int) (PlayerEntity.getReachDistance(false) - 0.5);
            BlockState targetedBlock = Util.targetedBlockState(client, reach);
            PlayerEntity player = client.player;

            // If no ender chest, end
            if (enderChestSlot == -1)
                return false;
            this.enderChestSlot = enderChestSlot;
            Util.swap(client, enderChestSlot, Util.THIRD_SLOT);
            player.getInventory().selectedSlot = Util.THIRD_SLOT;

            // Check whether you need to jump
            if (targetedBlock == null || !targetedBlock.isSolid()) {
                if (!player.isOnGround())
                    return true;
                Util.lookAtBlock(client, player.getBlockPos().down(), Direction.UP);
                player.jump();
                openContainer_y = player.getBlockPos().getY();
            }
            // Edge case, already looking at chest
            else if (targetedBlock.getBlock() == Blocks.ENDER_CHEST) {
                openContainer_phase = 1;
            }
            // Look at center of block
            else {
                BlockHitResult blockHitResult = Util.crosshairBlockHitResult(client, reach);
                if (blockHitResult == null)
                    return true;
                Util.lookAtBlock(client, blockHitResult.getBlockPos(), blockHitResult.getSide());
                openContainer_phase = 1;
            }
            openContainer_phase = 5;

            return true;
        }

        return false;
    }

    @Override
    public boolean openContainerEnd(MinecraftClient client, int selectedSlot) {
        return false;
    }

    @Override
    public boolean flyStart(MinecraftClient client, int selectedSlot) {
        return false;
    }

    @Override
    public boolean flyEnd(MinecraftClient client, int selectedSlot) {
        return false;
    }

    private BlockPos findNearbyEnderChest(PlayerEntity player, World world) {
        double reachDistance = player.getReachDistance(false) - 0.8;
        BlockPos playerPos = player.getBlockPos();
        Box searchBox = new Box(playerPos).expand(reachDistance);

        for (BlockPos pos : BlockPos.iterate((int) searchBox.getMin(Direction.Axis.X), (int) searchBox.getMin(Direction.Axis.Y), (int) searchBox.getMin(Direction.Axis.Z),
                (int) searchBox.getMax(Direction.Axis.X), (int) searchBox.getMax(Direction.Axis.Y), (int) searchBox.getMax(Direction.Axis.Z))) {
            if (world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST) {
                return pos;
            }
        }

        return null; // No EnderChest found within the reach distance
    }
}
