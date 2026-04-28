package astrotweaks.util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;


import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;


import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import astrotweaks.block.BlockQmBlock;
import astrotweaks.recipe.GavelRecipeRegistry;

import net.minecraftforge.fml.common.Mod.EventHandler;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

import astrotweaks.gui.GuiQAGUI;
import astrotweaks.gui.GuiMTGUI;
import astrotweaks.ark.ArkGUI;
//import astrotweaks.gui.QTPSupGUI;

import astrotweaks.qts.SuppressorEventHandler;



//import astrotweaks.world.CavernMobModifier;
import astrotweaks.AstrotweaksModVariables;






import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class Handler extends ElementsAstrotweaksMod.ModElement{


	public Handler(ElementsAstrotweaksMod instance) {
		//super(instance, 716);
		super(instance, 500);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		GavelRecipeRegistry.initDefaults();

		MinecraftForge.EVENT_BUS.register(new SuppressorEventHandler());

		
	}


	@Override
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);


	}

	
	@SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock() == BlockQmBlock.block) {
            event.setCanceled(true);
        }
    }


    public static class GuiHandler implements IGuiHandler {
		@Override
		public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
			if (id == GuiMTGUI.GUIID)
				return new GuiMTGUI.GuiContainerMod(world, x, y, z, player);
			if (id == GuiQAGUI.GUIID)
				return new GuiQAGUI.GuiContainerMod(world, x, y, z, player);
			if (id == ArkGUI.GUIID)
            	return null;
			//if (id == QTPSupGUI.GUIID)
            //	return null;
			return null;
		}

		@Override
		public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
			if (id == GuiMTGUI.GUIID)
				return new GuiMTGUI.GuiWindow(world, x, y, z, player);
			if (id == GuiQAGUI.GUIID)
				return new GuiQAGUI.GuiWindow(world, x, y, z, player);
			if (id == ArkGUI.GUIID)
                return new ArkGUI.GuiWindow(world, x, y, z, player);
			//if (id == QTPSupGUI.GUIID)
            //    return new QTPSupGUI.GuiWindow(world, x, y, z, player);
			return null;
		}
	}


    //
}
