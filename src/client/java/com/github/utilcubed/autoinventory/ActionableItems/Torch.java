package com.github.utilcubed.autoinventory.ActionableItems;

import com.github.utilcubed.autoinventory.Util;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class Torch implements ActionableItem {

    @Override
    public boolean update(MinecraftClient client, int selectedSlot) {
        return false;
    }

    @Override
    public boolean useStart(MinecraftClient client, int selectedSlot) {
        if (selectedSlot == Util.SECOND_SLOT) {
            BlockState blockState = Util.targetedBlockState(client, 6);
            if (blockState != null && !blockState.isAir() && blockState.getBlock() != Blocks.SCAFFOLDING) {
                int result = Util.findItemAndSwapTo(client, Items.TORCH, selectedSlot);
                return result != -1;
            }
        }
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

    public Torch() {
        super();
    }

}
