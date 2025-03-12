package me.Thelnfamous1.ip_command_stick.client.network;

import me.Thelnfamous1.ip_command_stick.client.CommandStickEditScreen;
import me.Thelnfamous1.ip_command_stick.network.S2COpenCommandStickEditorMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class IPCSClientNetHandler {

    public static void handle(S2COpenCommandStickEditorMessage s2COpenCommandStickEditorMessage) {
        ResourceLocation command = s2COpenCommandStickEditorMessage.command();
        Minecraft.getInstance().setScreen(new CommandStickEditScreen(s2COpenCommandStickEditorMessage.hand(), command == null ? "" : command.toString()));
    }
}
