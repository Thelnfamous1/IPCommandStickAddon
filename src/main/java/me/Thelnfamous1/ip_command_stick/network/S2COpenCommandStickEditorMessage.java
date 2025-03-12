package me.Thelnfamous1.ip_command_stick.network;

import me.Thelnfamous1.ip_command_stick.client.network.IPCSClientNetHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public record S2COpenCommandStickEditorMessage(InteractionHand hand, @Nullable ResourceLocation command) {

    public S2COpenCommandStickEditorMessage(FriendlyByteBuf buf){
        this(buf.readEnum(InteractionHand.class), buf.readNullable(FriendlyByteBuf::readResourceLocation));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(this.hand);
        buf.writeNullable(this.command, FriendlyByteBuf::writeResourceLocation);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        contextSupplier.get().enqueueWork(() -> {
            IPCSClientNetHandler.handle(this);
        });
        contextSupplier.get().setPacketHandled(true);
    }
}