package com.github.utilcubed.autoinventory.ActionableItems;

import com.github.utilcubed.autoinventory.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class Firework implements ActionableItem {

    private int swappedFromSlot = -1;
    private int swappedToSlot = -1;
    private int ticksBeforeSwap = 0;

    @Override
    public boolean update(MinecraftClient client, int selectedSlot) {
        if (ticksBeforeSwap > 1) {
            ticksBeforeSwap--;
        } else if (ticksBeforeSwap == 1) {
            if (client == null || client.player == null || client.currentScreen != null)
                return false;
            if (swappedFromSlot == -1 || swappedToSlot == -1) {
                Util.debug(client, "Error: swappedFromSlot or swappedToSlot is -1");
                return false;
            }

            Util.swap(client, swappedFromSlot, swappedToSlot);
            ticksBeforeSwap = 0;
            swappedFromSlot = -1;
            swappedToSlot = -1;

            return swappedFromSlot == selectedSlot || swappedToSlot == selectedSlot;

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
        return false;
    }

    @Override
    public boolean openContainerEnd(MinecraftClient client, int selectedSlot) {
        return false;
    }

    @Override
    public boolean flyStart(MinecraftClient client, int selectedSlot) {
        boolean mainHandAction = true;
        int swapToSlot = selectedSlot;

        if (swapToSlot != Util.SECOND_SLOT && swapToSlot != Util.THIRD_SLOT) {
            mainHandAction = false;
            swapToSlot = Util.THIRD_SLOT;
        }

        int swapFromSlot = Util.findItemAndSwapTo(client, Items.FIREWORK_ROCKET, swapToSlot);

        if (swapFromSlot == -1)
            return false;

        this.swappedFromSlot = swapFromSlot;
        this.swappedToSlot = swapToSlot;

        return mainHandAction;
    }

    @Override
    public boolean flyEnd(MinecraftClient client, int selectedSlot) {
        if (swappedFromSlot != -1 && swappedToSlot != -1)
            ticksBeforeSwap = 20;

        return false;
    }

    public Firework() {
        super();
    }
}
