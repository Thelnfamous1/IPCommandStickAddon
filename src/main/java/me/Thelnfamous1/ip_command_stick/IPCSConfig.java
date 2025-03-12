package me.Thelnfamous1.ip_command_stick;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

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

    public static boolean isCommandTypeEnabled(ResourceLocation location){
        if(!commandTypeBlacklistAsWhitelist.get()){
            return !commandTypeBlacklist.get().contains(location.toString());
        } else{
            return commandTypeBlacklist.get().contains(location.toString());
        }
    }
}
