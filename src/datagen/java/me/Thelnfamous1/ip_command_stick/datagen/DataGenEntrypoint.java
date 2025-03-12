package me.Thelnfamous1.ip_command_stick.datagen;

import me.Thelnfamous1.ip_command_stick.IPCommandStickMod;
import me.Thelnfamous1.ip_command_stick.client.CommandStickEditScreen;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import qouteall.imm_ptl.peripheral.platform_specific.PeripheralModEntry;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = IPCommandStickMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenEntrypoint {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // Example of adding a datagen provider for blockstates
        // generator.addProvider(event.includeClient(), new MyBlockStateProvider(generator.getPackOutput()));
        generator.addProvider(event.includeClient(), new LanguageProvider(generator.getPackOutput(), IPCommandStickMod.MODID, "en_us") {
            @Override
            protected void addTranslations() {
                this.add(CommandStickEditScreen.COMMAND_STICK_EDIT_COMPONENT, "Edit %s");
                this.add(CommandStickEditScreen.COMMAND_STICK_ERROR_INVALID_COMMAND_TYPE_COMPONENT, "Invalid %s command type: %s");
                this.add(CommandStickEditScreen.COMMAND_STICK_ERROR_NOT_HOLDING_COMMAND_STICK_COMPONENT, "You must be holding a %s to edit its command type.");
                this.add(CommandStickEditScreen.COMMAND_STICK_ERROR_COMMAND_TYPE_BLACKLISTED_COMPONENT, "%s is blacklisted from being applied to the %s.");
            }
        });
        generator.addProvider(event.includeServer(), new RecipeProvider(generator.getPackOutput()) {
            @Override
            protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PeripheralModEntry.COMMAND_STICK_ITEM.get())
                        .define('N', Items.NETHERITE_INGOT)
                        .define('S', Items.STICK)
                        .pattern(" N ")
                        .pattern(" S ")
                        .pattern(" N ")
                        .unlockedBy("has_stick", has(Items.STICK))
                        .unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                        .save(pWriter, IPCommandStickMod.resource(PeripheralModEntry.COMMAND_STICK_ITEM.getId().getPath()));
            }
        });
    }
}