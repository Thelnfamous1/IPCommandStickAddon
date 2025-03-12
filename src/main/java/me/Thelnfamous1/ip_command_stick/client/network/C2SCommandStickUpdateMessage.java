package me.Thelnfamous1.ip_command_stick.client.network;

import me.Thelnfamous1.ip_command_stick.IPCSConfig;
import me.Thelnfamous1.ip_command_stick.IPCommandStickMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record C2SCommandStickUpdateMessage(InteractionHand hand, ResourceLocation command) {

    public C2SCommandStickUpdateMessage(FriendlyByteBuf buf) {
        this(buf.readEnum(InteractionHand.class), buf.readResourceLocation());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(this.hand);
        buf.writeResourceLocation(this.command);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayer sender = contextSupplier.get().getSender();
            if(IPCSConfig.isCommandTypeEnabled(command)){
                IPCommandStickMod.updateCommandStickData(sender.getItemInHand(this.hand), this.command);
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }

}