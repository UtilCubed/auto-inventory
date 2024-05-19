package com.github.utilcubed.autoinventory.ActionableItems;

import com.github.utilcubed.autoinventory.Util;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class Bow implements ActionableItem {

    private int swappedFromSlot = -1;
    private int ticksBeforeSwap = 0;

    @Override
    public boolean update(MinecraftClient client, int selectedSlot) {
//        if (ticksBeforeSwap > 1) {
//            ticksBeforeSwap--;
//        } else if (ticksBeforeSwap == 1) {
//            if (client == null || client.player == null || client.currentScreen != null)
//                return false;
//            if (swappedFromSlot == -1 || Util.THIRD_SLOT == -1) {
//                Util.debug(client, "Error: swappedFromSlot or swappedToSlot is -1");
//                return false;
//            }
//
//            if (selectedSlot == Util.THIRD_SLOT && client.player.getMainHandStack().getItem() == Items.BOW) {
//                Util.swap(client, this.swappedFromSlot, Util.THIRD_SLOT);
//                this.swappedFromSlot = -1;
//                return true;
//            }
//            ticksBeforeSwap = 0;
//            Util.debug(client, "Inventory changed between bow start and end");
//
//            return swappedFromSlot == selectedSlot || Util.THIRD_SLOT == selectedSlot;
//
//        }
        return false;
    }

    @Override
    public boolean useStart(MinecraftClient client, int selectedSlot) {
        if (selectedSlot == Util.THIRD_SLOT) {
            BlockState targetedBlock = Util.targetedBlockState(client, 6);
            if (targetedBlock == null || (targetedBlock != null && targetedBlock.isAir())) {
                int result = Util.findItemAndSwapTo(client, Items.BOW, selectedSlot);
                this.swappedFromSlot = result;
                return result != -1;
            }
        }
        return false;
    }

    @Override
    public boolean useEnd(MinecraftClient client, int selectedSlot) {
//        if (selectedSlot == Util.THIRD_SLOT && swappedFromSlot != -1) {
//            this.ticksBeforeSwap = 40;
//            return true;
//        }

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

    public Bow() {
        super();
    }
}
