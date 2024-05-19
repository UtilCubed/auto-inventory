package com.github.utilcubed.autoinventory.ActionableItems;

import com.github.utilcubed.autoinventory.Util;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class Scalffolding implements ActionableItem {

    @Override
    public boolean update(MinecraftClient client, int selectedSlot) {
        return false;
    }

    @Override
    public boolean useStart(MinecraftClient client, int selectedSlot) {
//        BlockState targetedBlock = Util.targetedBlockState(client, 6);
//        if (targetedBlock != null && !targetedBlock.isAir())
//            return false;
//
//        boolean mainHandAction = true;
//        int swapToSlot = selectedSlot;
//
//        if (swapToSlot != Util.SECOND_SLOT && swapToSlot != Util.THIRD_SLOT) {
//            mainHandAction = false;
//            swapToSlot = Util.THIRD_SLOT;
//        }
//
//
//        int swapFromSlot = Util.findItemAndSwapTo(client, Items.ENDER_PEARL, swapToSlot);
//
//        if (swapFromSlot == -1)
//            return false;
//
//        return mainHandAction;
        //TODO: Add this in the future
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
        return false;
    }

    @Override
    public boolean flyEnd(MinecraftClient client, int selectedSlot) {
        return false;
    }

    public Scalffolding() {
        super();
    }
}
