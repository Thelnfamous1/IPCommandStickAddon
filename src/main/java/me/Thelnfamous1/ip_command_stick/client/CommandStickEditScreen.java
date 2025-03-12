package me.Thelnfamous1.ip_command_stick.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.Thelnfamous1.ip_command_stick.IPCSConfig;
import me.Thelnfamous1.ip_command_stick.IPCommandStickMod;
import me.Thelnfamous1.ip_command_stick.client.network.C2SCommandStickUpdateMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import qouteall.imm_ptl.peripheral.platform_specific.PeripheralModEntry;

public class CommandStickEditScreen extends Screen {
   private static final ResourceLocation EDIT_COMMAND_STICK_LOCATION = IPCommandStickMod.resource("textures/gui/edit_command_stick.png");
   public static final String COMMAND_STICK_EDIT_COMPONENT = "ip_command_stick.command_stick.edit";
   public static final String COMMAND_STICK_ERROR_INVALID_COMMAND_TYPE_COMPONENT = "ip_command_stick.command_stick.error.invalid_command_type";
   public static final String COMMAND_STICK_ERROR_NOT_HOLDING_COMMAND_STICK_COMPONENT = "ip_command_stick.command_stick.error.not_holding_command_stick";
   public static final String COMMAND_STICK_ERROR_COMMAND_TYPE_BLACKLISTED_COMPONENT = "ip_command_stick.command_stick.error.command_type_blacklisted";
   public static final String IMM_PTL_COMMAND_STICK_COMPONENT = "imm_ptl.command_stick";

   private final int imageWidth = 176 + 80;
   private final int imageHeight = 48;
   private int leftPos;
   private int topPos;
   private final int titleLabelX = 60;
   private final int titleLabelY = 8;
   private final InteractionHand hand;
   private String command;
   private EditBox commandEdit;
   CommandStickSuggestions commandSuggestions;

   public CommandStickEditScreen(InteractionHand hand, String command) {
      super(Component.translatable(COMMAND_STICK_EDIT_COMPONENT, Component.translatable(IMM_PTL_COMMAND_STICK_COMPONENT)));
      this.hand = hand;
      this.command = command;
   }

   @Override
   protected void init() {
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = this.height / 4;
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onDone()).bounds(this.width / 2 - 100, this.height / 4 + 120, 200, 20).build());
      this.commandEdit = new EditBox(this.font, this.leftPos + 62, this.topPos + 26, 103 + 80, 12, Component.translatable("advMode.command")){

         @Override
         protected MutableComponent createNarrationMessage() {
            MutableComponent narrationMessage = super.createNarrationMessage();
            if(CommandStickEditScreen.this.commandSuggestions != null) narrationMessage = narrationMessage.append(CommandStickEditScreen.this.commandSuggestions.getNarrationMessage());
            return narrationMessage;
         }
      };
      //this.commandEdit.setCanLoseFocus(false);
      this.commandEdit.setTextColor(-1);
      this.commandEdit.setTextColorUneditable(-1);
      this.commandEdit.setBordered(false);
      this.commandEdit.setMaxLength(32500);
      this.commandEdit.setResponder(this::onEdited);
      this.commandEdit.setValue(this.command);
      this.addWidget(this.commandEdit);
      this.setInitialFocus(this.commandEdit);
      // Command Suggestions
      /*
      this.commandSuggestions = new CommandStickSuggestions(this.minecraft, this, this.commandEdit, this.font, true, 0, 7, false, Integer.MIN_VALUE);
      this.commandSuggestions.setAllowSuggestions(true);
      this.commandSuggestions.updateCommandInfo();
       */
   }

   private void onDone() {
      if(!this.command.contains(":")){
         this.command = IPCommandStickMod.IMM_PTL_MODID + ":" + this.command;
      }
      ResourceLocation parsedCommand = ResourceLocation.tryParse(this.command);
      if(parsedCommand == null || IPCommandStickMod.getDataByName(parsedCommand) == null){
         this.minecraft.player.sendSystemMessage(
                 Component.translatable(COMMAND_STICK_ERROR_INVALID_COMMAND_TYPE_COMPONENT,
                         Component.translatable(IMM_PTL_COMMAND_STICK_COMPONENT),
                         parsedCommand == null ? this.command : parsedCommand.toString()).withStyle(ChatFormatting.RED));
      } else{
         if(!IPCSConfig.isCommandTypeEnabled(parsedCommand)){
            this.minecraft.player.sendSystemMessage(
                    Component.translatable(COMMAND_STICK_ERROR_COMMAND_TYPE_BLACKLISTED_COMPONENT,
                            parsedCommand.toString(),
                            Component.translatable(IMM_PTL_COMMAND_STICK_COMPONENT)).withStyle(ChatFormatting.RED));
         } else{
            if(!this.minecraft.player.getItemInHand(this.hand).is(PeripheralModEntry.COMMAND_STICK_ITEM.get())){
               this.minecraft.player.sendSystemMessage(Component.translatable(
                       COMMAND_STICK_ERROR_NOT_HOLDING_COMMAND_STICK_COMPONENT,
                       Component.translatable(IMM_PTL_COMMAND_STICK_COMPONENT)).withStyle(ChatFormatting.RED));
            } else{
               // Update server copy
               IPCommandStickMod.NETWORK.sendToServer(new C2SCommandStickUpdateMessage(this.hand, parsedCommand));
            }
         }
      }
      this.onClose();
   }

   private void onEdited(String command) {
      this.command = command;
      if(this.commandSuggestions != null) this.commandSuggestions.updateCommandInfo();
   }

   @Override
   public void tick() {
      this.commandEdit.tick();
   }

   @Override
   public void resize(Minecraft minecraft, int width, int height) {
      String s = this.commandEdit.getValue();
      this.init(minecraft, width, height); // re-creates the command edit and command suggestions
      this.commandEdit.setValue(s);
      if(this.commandSuggestions != null) this.commandSuggestions.updateCommandInfo();
   }

   @Override
   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      this.renderBackground(guiGraphics);
      this.renderBg(guiGraphics, partialTick, mouseX, mouseY);
      super.render(guiGraphics, mouseX, mouseY, partialTick);
      guiGraphics.drawString(this.font, this.title, this.leftPos + this.titleLabelX, this.topPos + this.titleLabelY, 4210752, false);
      this.commandEdit.render(guiGraphics, mouseX, mouseY, partialTick);
      if(this.commandSuggestions != null) this.commandSuggestions.render(guiGraphics, mouseX, mouseY);
   }

   protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      guiGraphics.blit(EDIT_COMMAND_STICK_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      guiGraphics.pose().pushPose();
      guiGraphics.pose().scale(2.0F, 2.0F, 2.0F);
      guiGraphics.renderItem(new ItemStack(PeripheralModEntry.COMMAND_STICK_ITEM.get()), (this.leftPos + 17) / 2, (this.topPos + 8) / 2);
      guiGraphics.pose().popPose();
   }

   @Override
   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (this.commandSuggestions != null && this.commandSuggestions.keyPressed(pKeyCode, pScanCode, pModifiers)) {
         return true;
      } else if (super.keyPressed(pKeyCode, pScanCode, pModifiers)) {
         return true;
      } else if (pKeyCode != GLFW.GLFW_KEY_ENTER && pKeyCode != GLFW.GLFW_KEY_KP_ENTER) {
         return false;
      } else {
         this.onDone();
         return true;
      }
   }

   @Override
   public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
      return this.commandSuggestions != null && this.commandSuggestions.mouseScrolled(pDelta) || super.mouseScrolled(pMouseX, pMouseY, pDelta);
   }

   @Override
   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      return this.commandSuggestions != null && this.commandSuggestions.mouseClicked(pMouseX, pMouseY, pButton) || super.mouseClicked(pMouseX, pMouseY, pButton);
   }
}