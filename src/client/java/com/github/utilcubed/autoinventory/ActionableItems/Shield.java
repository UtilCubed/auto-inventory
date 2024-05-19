package com.github.utilcubed.autoinventory.ActionableItems;

import com.github.utilcubed.autoinventory.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;

public class Shield implements ActionableItem {

    private int swappedFromSlot = -1;

    @Override
    public boolean update(MinecraftClient client, int selectedSlot) {
        return false;
    }

    @Override
    public boolean useStart(MinecraftClient client, int selectedSlot) {
        if (client == null || client.player == null || client.currentScreen != null)
            return false;

        if (selectedSlot == Util.FIRST_SLOT) {
            int result = Util.findItemAndSwapTo(client, Items.SHIELD, PlayerInventory.OFF_HAND_SLOT);
            this.swappedFromSlot = result;
            return result != -1;
        }

        return false;
    }

    @Override
    public boolean useEnd(MinecraftClient client, int selectedSlot) {
        if (swappedFromSlot != -1) {
            Util.swap(client, this.swappedFromSlot, PlayerInventory.OFF_HAND_SLOT);
            this.swappedFromSlot = -1;
            return true;
        }
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

    public Shield() {
        super();
    }
}
