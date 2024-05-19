package com.github.utilcubed.autoinventory.ActionableItems;

import com.github.utilcubed.autoinventory.Util;
import net.minecraft.client.MinecraftClient;

public class SecondaryTool implements ActionableItem {


    @Override
    public boolean update(MinecraftClient client, int selectedSlot) {
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
        return false;
    }

    @Override
    public boolean flyEnd(MinecraftClient client, int selectedSlot) {
        return false;
    }

    public SecondaryTool() {
        super();
    }
}