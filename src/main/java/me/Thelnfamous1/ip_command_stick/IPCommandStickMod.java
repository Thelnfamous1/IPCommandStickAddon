package me.Thelnfamous1.ip_command_stick;

import com.mojang.logging.LogUtils;
import me.Thelnfamous1.ip_command_stick.client.network.C2SCommandStickUpdateMessage;
import me.Thelnfamous1.ip_command_stick.network.S2COpenCommandStickEditorMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import qouteall.imm_ptl.peripheral.CommandStickItem;
import qouteall.imm_ptl.peripheral.platform_specific.PeripheralModEntry;

import java.util.Optional;

@Mod(IPCommandStickMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class IPCommandStickMod {
    public static final String MODID = "ip_command_stick";
    public static final String IMM_PTL_MODID = "imm_ptl";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String NETWORK_VERSION = "1.0";
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(resource("sync_channel"),
            () -> NETWORK_VERSION,
            s -> s.equals(NETWORK_VERSION),
            c -> c.equals(NETWORK_VERSION));
    private static int index = 0;

    public IPCommandStickMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, IPCSConfig.SERVER_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().register(IPCSConfig.class);
    }

    public static ResourceLocation resource(String path){
        return new ResourceLocation(MODID, path);
    }

    @Nullable
    public static ResourceLocation getNameByData(ItemStack stack){
        CommandStickItem.Data heldData = CommandStickItem.Data.deserialize(stack.getOrCreateTag());
        for(ResourceLocation name : CommandStickItem.REGISTRY.get().getKeys()){
            // Expecting ["imm_ptl", "command", <name of the command>]
            String[] nameTranslationKeyArray = heldData.nameTranslationKey.split("\\.", 3);
            if(nameTranslationKeyArray.length >= 3
                    && name.getNamespace().equals(nameTranslationKeyArray[0]) // same namesapce
                    && name.getPath().equals(nameTranslationKeyArray[2])){ // same path
                return name;
            }
        }
        return null;
    }

    @Nullable
    public static CommandStickItem.Data getDataByName(ResourceLocation name){
        if(!FMLEnvironment.production) LOGGER.info("Querying name {} from command stick type registry", name);
        return CommandStickItem.REGISTRY.get().getValue(name);
    }

    

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        // Do something when the setup is run on both client and server
        // LOGGER.info("HELLO from common setup!");
        event.enqueueWork(() -> {
           NETWORK.registerMessage(index++, S2COpenCommandStickEditorMessage.class, S2COpenCommandStickEditorMessage::write, S2COpenCommandStickEditorMessage::new, S2COpenCommandStickEditorMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
            NETWORK.registerMessage(index++, C2SCommandStickUpdateMessage.class, C2SCommandStickUpdateMessage::write, C2SCommandStickUpdateMessage::new, C2SCommandStickUpdateMessage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        });
    }

    public static void updateCommandStickData(ItemStack stack, ResourceLocation command) {
        if (stack.is(PeripheralModEntry.COMMAND_STICK_ITEM.get())) {
            CommandStickItem.Data data = getDataByName(command);
            if(data != null){
                if(!FMLEnvironment.production) LOGGER.info("Writing data {} to {}", Component.translatable(data.nameTranslationKey).getString(), stack);
                data.serialize(stack.getOrCreateTag());
            }
        }
    }
}
