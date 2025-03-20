package me.Thelnfamous1.ip_command_stick;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IPCSConfig {
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        SERVER_SPEC = configBuilder.build();
    }
    private static ForgeConfigSpec.BooleanValue commandTypeBlacklistAsWhitelist;
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> commandTypeBlacklist;

    private static void setupConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Blacklist configs for the command stick").push("Blacklist Configs");
        commandTypeBlacklist = builder
                .comment("Define command types that cannot be applied to the command stick via the GUI.")
                .defineList("command_type_blacklist", ArrayList::new, entry -> entry instanceof String resource && ResourceLocation.tryParse(resource) != null);
        commandTypeBlacklistAsWhitelist = builder.comment("Use the command type blacklist as a whitelist instead.").define("command_type_blacklist_as_whitelist", false);
        builder.pop();
    }

    private static final Set<ResourceLocation> builtCommandTypeBlacklist = new HashSet<>();

    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event){
        if(event.getConfig().getSpec() == SERVER_SPEC){
            buildCommandTypeBlacklistSet();
        }
    }

    private static void buildCommandTypeBlacklistSet() {
        builtCommandTypeBlacklist.clear();
        for(String entry : commandTypeBlacklist.get()){
            if(!entry.contains(":")){
                entry = IPCommandStickMod.IMM_PTL_MODID + ":" + entry;
            }
            ResourceLocation commandTypeKey = ResourceLocation.tryParse(entry);
            if(commandTypeKey != null){
                builtCommandTypeBlacklist.add(commandTypeKey);
            } else{
                IPCommandStickMod.LOGGER.info("Could not parse command type {} for the command type {}.", entry, commandTypeBlacklistAsWhitelist.get() ? "whitelist" : "blacklist");
            }
        }
    }

    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event){
        if(event.getConfig().getSpec() == SERVER_SPEC){
            buildCommandTypeBlacklistSet();
        }
    }

    public static boolean isCommandTypeEnabled(ResourceLocation location){
        if(!commandTypeBlacklistAsWhitelist.get()){
            return !builtCommandTypeBlacklist.contains(location);
        } else{
            return builtCommandTypeBlacklist.contains(location);
        }
    }
}
