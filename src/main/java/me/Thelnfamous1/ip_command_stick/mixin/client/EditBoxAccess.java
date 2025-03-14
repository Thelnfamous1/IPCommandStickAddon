package me.Thelnfamous1.ip_command_stick.mixin.client;

import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EditBox.class)
public interface EditBoxAccess {

    @Invoker("isBordered")
    boolean ip_command_stick$isBordered();
}
