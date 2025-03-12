package me.Thelnfamous1.ip_command_stick.mixin;

import me.Thelnfamous1.ip_command_stick.IPCommandStickMod;
import me.Thelnfamous1.ip_command_stick.network.S2COpenCommandStickEditorMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qouteall.imm_ptl.peripheral.CommandStickItem;

@Mixin(CommandStickItem.class)
public abstract class CommandStickItemMixin extends Item {

    public CommandStickItemMixin(Properties pProperties) {
        super(pProperties);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void pre_use(Level world, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir){
        if(player.isSecondaryUseActive()){
            ItemStack itemInHand = player.getItemInHand(hand);
            if(player instanceof ServerPlayer serverPlayer){
                IPCommandStickMod.NETWORK.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new S2COpenCommandStickEditorMessage(hand, IPCommandStickMod.getNameByData(itemInHand)));
            }
            cir.setReturnValue(InteractionResultHolder.sidedSuccess(itemInHand, world.isClientSide));
        }
    }
}
