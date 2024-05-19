package com.github.utilcubed.autoinventory.ActionableItems;

import com.github.utilcubed.autoinventory.Util;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class Food implements ActionableItem {

    private int swappedFromSlot = -1;
    private int ticksBeforeSwap = 0;

    @Override
    public boolean update(MinecraftClient client, int selectedSlot) {
        if (ticksBeforeSwap > 1) {
            ticksBeforeSwap--;
        } else if (ticksBeforeSwap == 1) {
            if (client == null || client.player == null || client.currentScreen != null)
                return false;
            if (swappedFromSlot == -1) {
                Util.debug(client, "Error: swappedFromSlot or swappedToSlot is -1");
                return false;
            }

            Util.swap(client, swappedFromSlot, PlayerInventory.OFF_HAND_SLOT);
            ticksBeforeSwap = 0;
            swappedFromSlot = -1;

            return true;
        }
        return false;
    }

    @Override
    public boolean useStart(MinecraftClient client, int selectedSlot) {
        if (selectedSlot < 3)
            return false;

        // If air
        BlockState blockState = Util.targetedBlockState(client, 6);
        if (blockState == null || blockState.isAir()) {
            int foodSlot = Util.getSlotOfItem(client, Items.GOLDEN_CARROT);
            if (foodSlot == -1)
                Util.getSlotOfItem(client, Items.COOKED_BEEF);
            if (foodSlot == -1)
                Util.getSlotOfItem(client, Items.COOKED_PORKCHOP);
            if (foodSlot == -1)
                return false;
            Util.swap(client, foodSlot, PlayerInventory.OFF_HAND_SLOT);
            this.swappedFromSlot = foodSlot;
        }
        return true;
    }

    @Override
    public boolean useEnd(MinecraftClient client, int selectedSlot) {
        if (swappedFromSlot != -1)
            ticksBeforeSwap = 10;

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

    public Food() {
        super();
    }
}
