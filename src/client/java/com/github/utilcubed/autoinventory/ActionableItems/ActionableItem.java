package com.github.utilcubed.autoinventory.ActionableItems;

import net.minecraft.client.MinecraftClient;

public interface ActionableItem {

    boolean update(MinecraftClient client, int selectedSlot);

    boolean useStart(MinecraftClient client, int selectedSlot);
    boolean useEnd(MinecraftClient client, int selectedSlot);

    boolean attackStart(MinecraftClient client, int selectedSlot);
    boolean attackEnd(MinecraftClient client, int selectedSlot);

    boolean openContainerStart(MinecraftClient client, int selectedSlot);
    boolean openContainerEnd(MinecraftClient client, int selectedSlot);

    boolean flyStart(MinecraftClient client, int selectedSlot);
    boolean flyEnd(MinecraftClient client, int selectedSlot);

}
