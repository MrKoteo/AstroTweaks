package astrotweaks.ark;

import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Keyboard;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.fml.common.FMLCommonHandler;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;
import java.lang.Runnable;
import java.io.IOException;

import astrotweaks.ark.BlockArk;
import astrotweaks.ark.ArkTransferHelper;

import astrotweaks.ElementsAstrotweaksMod;
import astrotweaks.AstrotweaksMod;

import javax.sound.sampled.TargetDataLine;

@ElementsAstrotweaksMod.ModElement.Tag
public class ArkGUI extends ElementsAstrotweaksMod.ModElement {
	public static int GUIID = 10;
	public static HashMap guistate = new HashMap();
	public ArkGUI(ElementsAstrotweaksMod instance) {super(instance, 720);}
	// GUI have:  4 button "SEND", 5 number fields, Text title of GUI.

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		elements.addNetworkMessage(GUIButtonPressedMessageHandler.class, GUIButtonPressedMessage.class, Side.SERVER);
		elements.addNetworkMessage(ArkActionMessageHandler.class, ArkActionMessage.class, Side.SERVER);

	}

	// Message for transferring data from the GUI
	public static class ArkActionMessage implements IMessage {
	    int buttonID;
	    int blockX, blockY, blockZ;
	    String dimStr;
	    String xStr, yStr, zStr, delay;
	    private boolean clearMode;
	    private boolean captureEntities;
	    private boolean captureItems;
	    //private int delayTicks;

	    public ArkActionMessage() {}

		public ArkActionMessage(int buttonID, int blockX, int blockY, int blockZ, String dim, String x, String y, String z,
 					boolean clearMode, boolean captureEntities, boolean captureItems, String delayTicks) {
	        this.buttonID = buttonID;
	        this.blockX = blockX; this.blockY = blockY; this.blockZ = blockZ;
	        this.dimStr = dim; this.xStr = x; this.yStr = y; this.zStr = z; this.delay = delayTicks;
	        this.clearMode = clearMode;
	        this.captureEntities = captureEntities;
	        this.captureItems = captureItems;
	        //this.delayTicks = delayTicks;
	    }
	    @Override
	    public void toBytes(ByteBuf buf) {
	        buf.writeInt(buttonID);
	        buf.writeInt(blockX); buf.writeInt(blockY); buf.writeInt(blockZ);
	        writeString(buf, dimStr); writeString(buf, xStr); writeString(buf, yStr); writeString(buf, zStr);
	        buf.writeBoolean(clearMode);
	        buf.writeBoolean(captureEntities);
	        buf.writeBoolean(captureItems);
	        writeString(buf, delay);
	    }
	    @Override
	    public void fromBytes(ByteBuf buf) {
	        buttonID = buf.readInt();
	        blockX = buf.readInt(); blockY = buf.readInt(); blockZ = buf.readInt();
	        dimStr = readString(buf); xStr = readString(buf); yStr = readString(buf); zStr = readString(buf);
	        clearMode = buf.readBoolean();
	        captureEntities = buf.readBoolean();
	        captureItems = buf.readBoolean();
	        delay = readString(buf);
	    }
	    private void writeString(ByteBuf buf, String s) {
	        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
	        buf.writeInt(bytes.length);
	        buf.writeBytes(bytes);
	    }
	    private String readString(ByteBuf buf) {
	        int len = buf.readInt();
	        byte[] bytes = new byte[len];
	        buf.readBytes(bytes);
	        return new String(bytes, StandardCharsets.UTF_8);
	    }
	}

	// Message handler
	public static class ArkActionMessageHandler implements IMessageHandler<ArkActionMessage, IMessage> {
	    @Override
	    public IMessage onMessage(ArkActionMessage message, MessageContext context) {
	        EntityPlayerMP player = context.getServerHandler().player;
	        player.getServerWorld().addScheduledTask(() -> {
	            World world = player.world;
	            BlockPos pos = new BlockPos(message.blockX, message.blockY, message.blockZ);
	            if (!world.isBlockLoaded(pos)) return;

	            TileEntity te = world.getTileEntity(pos);
	            if (!(te instanceof BlockArk.TileEntityCustom)) return;
	            BlockArk.TileEntityCustom teArk = (BlockArk.TileEntityCustom) te;

	            // Parse strings
	            int targetDim = 0, targetX = 0, targetY = 65, targetZ = 0, delayTicks = 0;
	            boolean parseOk = true;
	            try {
	                targetDim = Integer.parseInt(message.dimStr);
	                targetX = Integer.parseInt(message.xStr);
	                targetY = Integer.parseInt(message.yStr);
	                targetZ = Integer.parseInt(message.zStr);
	                delayTicks = Integer.parseInt(message.delay);
	            } catch (NumberFormatException e) {
	                parseOk = false;
	                if (message.buttonID == 0) {
	                    player.sendMessage(new TextComponentString(TextFormatting.RED + "Error: coordinates must be numbers."));
	                }
	            }
	
				//System.out.println("[Ark] Received message: buttonID=" + message.buttonID + ", dim=" + message.dimStr + ", x=" + message.xStr + ", y=" + message.yStr + ", z=" + message.zStr + ", delay=" + message.delay + ", parseOk=" + parseOk);

	            if (message.buttonID == 1) {
	                
	                if (parseOk) {
	                    teArk.setTargetDim(targetDim);
	                    teArk.setTargetX(targetX);
	                    teArk.setTargetY(targetY);
	                    teArk.setTargetZ(targetZ);
	                }
	                teArk.setClearMode(message.clearMode);
	                teArk.setCaptureEntities(message.captureEntities);
	                teArk.setCaptureItems(message.captureItems);
	                teArk.setDelayTicks(delayTicks);
	                return;
	            }

				// Start transfer with delay
	            if (message.buttonID == 0 && parseOk) {
	                //if (delayTicks > 0) {
	                // delay with TileEntity (ITickable)
					if (delayTicks < 1) { delayTicks = 1;}
					if (!(delayTicks == 1)) { player.sendMessage(new TextComponentString(TextFormatting.AQUA + "Transfer will start in " + delayTicks + " ticks.")); }
                    
                    teArk.startDelayedTransfer(player, pos, targetDim, targetX,targetY,targetZ, message.clearMode, message.captureEntities, message.captureItems, delayTicks);
                    
	                //} else {
	                //    // instant transfer
	                //    ArkTransferHelper.performTeleport(player, world, pos, teArk,
	                //            targetDim, targetX, targetY, targetZ,
	                //            message.clearMode, message.captureEntities, message.captureItems);
	                //}
	            }
	        });
	        return null;
	    }

	}

	public static class GuiWindow extends GuiScreen {
	    private World world;
	    private int x, y, z;
	    private EntityPlayer entity;
	    private BlockArk.TileEntityCustom teArk;
	    private GuiTextField TargetDimID;

	    private boolean clearMode = true;
	    private boolean captureEntities = true;
	    private boolean captureItems = true;
	    //private int delayTicks = 0;

	    private GuiButton btnClearMode;
	    private GuiButton btnCaptureEntities;
	    private GuiButton btnCaptureItems;

	    private GuiTextField TW_X;
	    private GuiTextField TW_Y;
	    private GuiTextField TW_Z;
	    private GuiTextField delayField;
	    private static final ResourceLocation texture = new ResourceLocation("astrotweaks:textures/ark_gui_modern.png");

		public GuiWindow(World world, int x, int y, int z, EntityPlayer entity) {
			this.world = world;
			this.x = x; this.y = y; this.z = z;
			this.entity = entity;
		}
	    @Override
	    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
	        this.drawDefaultBackground();
	        // Draw the background texture
	        this.mc.renderEngine.bindTexture(texture);
	        int k = (this.width - 200) / 2;
	        int l = (this.height - 140) / 2;
	        drawModalRectWithCustomSizedTexture(k, l, 0, 0, 200, 140, 200, 140);
	        // Draw text fields and buttons
	        super.drawScreen(mouseX, mouseY, partialTicks);
	        TargetDimID.drawTextBox();
	        TW_X.drawTextBox();
	        TW_Y.drawTextBox();
	        TW_Z.drawTextBox();
	        delayField.drawTextBox();
	        // Draw the title
	        fontRenderer.drawString("Dimensional Ark Interface", k + 40, l + 5, 0x8020FF);
	    }
	    @Override
	    public void updateScreen() {
	        super.updateScreen();
	        TargetDimID.updateCursorCounter();
	        TW_X.updateCursorCounter();
	        TW_Y.updateCursorCounter();
	        TW_Z.updateCursorCounter();
	        delayField.updateCursorCounter();
	    }
	    @Override
	    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
	        super.mouseClicked(mouseX, mouseY, mouseButton);
	        int k = (this.width - 200) / 2;
	        int l = (this.height - 140) / 2;
		    TargetDimID.mouseClicked(mouseX, mouseY, mouseButton);
		    TW_X.mouseClicked(mouseX, mouseY, mouseButton);
		    TW_Y.mouseClicked(mouseX, mouseY, mouseButton);
		    TW_Z.mouseClicked(mouseX, mouseY, mouseButton);
		    delayField.mouseClicked(mouseX, mouseY, mouseButton);
	    }
	    @Override
	    protected void keyTyped(char typedChar, int keyCode) throws IOException {
	        if (TargetDimID.textboxKeyTyped(typedChar, keyCode)) return;
	        if (TW_X.textboxKeyTyped(typedChar, keyCode)) return;
	        if (TW_Y.textboxKeyTyped(typedChar, keyCode)) return;
	        if (TW_Z.textboxKeyTyped(typedChar, keyCode)) return;
	        if (delayField.textboxKeyTyped(typedChar, keyCode)) return;
	        super.keyTyped(typedChar, keyCode);
	    }
	    @Override
	    public void initGui() {
	        super.initGui();
	        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
	        if (te instanceof BlockArk.TileEntityCustom) {
	            teArk = (BlockArk.TileEntityCustom) te;
	        }
	        int k = (this.width - 200) / 2;
	        int l = (this.height - 140) / 2;
	        Keyboard.enableRepeatEvents(true);
	        buttonList.clear();
		
		    if (teArk != null) {
		        clearMode = teArk.getClearMode();
		        captureEntities = teArk.getCaptureEntities();
		        captureItems = teArk.getCaptureItems();
		        //delayTicks = teArk.getDelayTicks();
		    }

		    TargetDimID = new GuiTextField(0, fontRenderer, k + 14, l + 20, 45, 12);
		    TargetDimID.setMaxStringLength(8);
		    TargetDimID.setText(teArk != null ? String.valueOf(teArk.getTargetDim()) : "0");

		    TW_X = new GuiTextField(1, fontRenderer, k + 14, l + 44, 45, 12);
		    TW_X.setMaxStringLength(8);
		    TW_X.setText(teArk != null ? String.valueOf(teArk.getTargetX()) : "0");

		    TW_Y = new GuiTextField(2, fontRenderer, k + 14, l + 68, 45, 12);
		    TW_Y.setMaxStringLength(8);
		    TW_Y.setText(teArk != null ? String.valueOf(teArk.getTargetY()) : "65");

		    TW_Z = new GuiTextField(3, fontRenderer, k + 14, l + 92, 45, 12);
		    TW_Z.setMaxStringLength(8);
		    TW_Z.setText(teArk != null ? String.valueOf(teArk.getTargetZ()) : "0");

		    btnClearMode = new GuiButton(1, k + 90, l + 20, 48, 18, getClearModeText());
		    buttonList.add(btnClearMode);

		    btnCaptureEntities = new GuiButton(2, k + 90, l + 44, 48, 18, getCaptureEntitiesText());
		    buttonList.add(btnCaptureEntities);

		    btnCaptureItems = new GuiButton(3, k + 90, l + 68, 48, 18, getCaptureItemsText());
		    buttonList.add(btnCaptureItems);
		
		    delayField = new GuiTextField(4, fontRenderer, k + 14, l + 116, 45, 12);
		    delayField.setMaxStringLength(6);
		    delayField.setText(teArk != null ? String.valueOf(teArk.getDelayTicks()) : "0");

			updateButtonsDisplay();

	        buttonList.add(new GuiButton(0, k + 150, l + 55, 36, 20, "SEND"));
	    }

	    private String getClearModeText() {
	        return clearMode ? "Destroy" : "Replace";
	    }
	    private String getCaptureEntitiesText() {
	        return captureEntities ? "Entities: ON" : "Entities: OFF";
	    }
	    private String getCaptureItemsText() {
	        return captureItems ? "Items: ON" : "Items: OFF";
	    }
		private int parseIntSafe(String s, int defaultValue) {
		    try {
		        return Integer.parseInt(s);
		    } catch (NumberFormatException e) {
		        return defaultValue;
		    }
		}
		private void updateButtonsDisplay() {
		    if (btnClearMode != null) btnClearMode.displayString = getClearModeText();
		    if (btnCaptureEntities != null) btnCaptureEntities.displayString = getCaptureEntitiesText();
		    if (btnCaptureItems != null) btnCaptureItems.displayString = getCaptureItemsText();
		}

	    @Override
	    protected void actionPerformed(GuiButton button) throws IOException {
	        if (button.id == 0) { // SEND
	            mc.player.closeScreen();
	            AstrotweaksMod.PACKET_HANDLER.sendToServer(new ArkActionMessage(
	                0, x, y, z,
	                TargetDimID.getText(),
	                TW_X.getText(),
	                TW_Y.getText(),
	                TW_Z.getText(),
	                clearMode,
	                captureEntities,
	                captureItems,
	                delayField.getText()
	            ));
	        } else if (button.id == 1) { // Clear Mode toggle
	            clearMode = !clearMode;
	            btnClearMode.displayString = getClearModeText();
	        } else if (button.id == 2) { // Entities toggle
	            captureEntities = !captureEntities;
	            if (!captureEntities) {
	                captureItems = false; // automatically turn off items
	                btnCaptureItems.displayString = getCaptureItemsText();
	            }
	            btnCaptureEntities.displayString = getCaptureEntitiesText();
	        } else if (button.id == 3) { // Items toggle
	            if (captureEntities) { // can only be switched if entities are enabled
	                captureItems = !captureItems;
	                btnCaptureItems.displayString = getCaptureItemsText();
	            }
	        }
	    }
	    @Override
	    public void onGuiClosed() {
	        super.onGuiClosed();
	        AstrotweaksMod.PACKET_HANDLER.sendToServer(new ArkActionMessage(
	            1, x, y, z,
	            TargetDimID.getText(),
	            TW_X.getText(),
	            TW_Y.getText(),
	            TW_Z.getText(),
	            clearMode,
	            captureEntities,
	            captureItems,
	            delayField.getText()
	        ));
	        Keyboard.enableRepeatEvents(false);
	    }
	    @Override public boolean doesGuiPauseGame() { return false; }
	}
	public static class GUIButtonPressedMessageHandler implements IMessageHandler<GUIButtonPressedMessage, IMessage> {
		@Override
		public IMessage onMessage(GUIButtonPressedMessage message, MessageContext context) {
			EntityPlayerMP entity = context.getServerHandler().player;
			entity.getServerWorld().addScheduledTask(() -> {
				int buttonID = message.buttonID;
				int x = message.x;
				int y = message.y;
				int z = message.z;
				handleButtonAction(entity, buttonID, x, y, z);
			});
			return null;
		}
	}
	public static class GUIButtonPressedMessage implements IMessage {
		int buttonID, x, y, z;
		public GUIButtonPressedMessage() {}
		public GUIButtonPressedMessage(int buttonID, int x, int y, int z) {
			this.buttonID = buttonID;
			this.x = x; this.y = y; this.z = z;
		}
		@Override public void toBytes(io.netty.buffer.ByteBuf buf) {
			buf.writeInt(buttonID);
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeInt(z);
		}
		@Override public void fromBytes(io.netty.buffer.ByteBuf buf) {
			buttonID = buf.readInt();
			x = buf.readInt();
			y = buf.readInt();
			z = buf.readInt();
		}
	}
	private static void handleButtonAction(EntityPlayer entity, int buttonID, int x, int y, int z) {
		World world = entity.world;
		if (!world.isBlockLoaded(new BlockPos(x, y, z))) return;
	}
}
