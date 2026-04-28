package astrotweaks.procedure;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.Entity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.Random;
import java.util.Map;

import astrotweaks.item.*;
//import astrotweaks.item.ItemCordageVine;
import astrotweaks.world.DepthsDim;

import astrotweaks.ElementsAstrotweaksMod;




@ElementsAstrotweaksMod.ModElement.Tag
public final class ProcedureEventBreakBlock extends ElementsAstrotweaksMod.ModElement {
    private static final int CAVERN_DIM_ID = DepthsDim.DIMID;
	public ProcedureEventBreakBlock(ElementsAstrotweaksMod instance) { super(instance, 340); }
	private static void spawnItem(World world, int x, int y, int z, ItemStack stack) {
	  if (world == null || stack == null || stack.isEmpty() || world.isRemote) return;
	  EntityItem ei = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack);
	  ei.setPickupDelay(10);
	  world.spawnEntity(ei);
	}
	public static void executeProcedure(World world, int x, int y, int z, Entity entity, BlockEvent.BreakEvent event) {
		if (world == null || world.isRemote) return;

		Random rand = world.rand;
		double rng1 = rand.nextDouble();
		double RNM = 0;

		BlockPos pos = new BlockPos(x, y, z);
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		Material mat = state.getMaterial();

		//ItemStack held = (entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemMainhand() : ItemStack.EMPTY;
		//boolean notShears = !(held.getItem() == new ItemStack(Items.SHEARS, 1).getItem());
		ItemStack held = entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).getHeldItemMainhand() : ItemStack.EMPTY;
		boolean notShears = held.getItem() != Items.SHEARS;
		
		boolean notCreativeMode = true;
		if (entity instanceof EntityPlayer) notCreativeMode = !((EntityPlayer)entity).capabilities.isCreativeMode;
		//net.minecraft.block.Block block = world.getBlockState(pos).getBlock();

		int dim = world.provider.getDimension();

		//boolean isTallGrassVariant = block == Blocks.TALLGRASS.getStateFromMeta(1).getBlock() || block == Blocks.DOUBLE_PLANT.getStateFromMeta(2).getBlock();
		boolean isTallGrassVariant = block == Blocks.TALLGRASS && state.getValue(BlockTallGrass.TYPE) == BlockTallGrass.EnumType.GRASS;

		if (notCreativeMode) {
		if (isTallGrassVariant && notShears) {
			if (rng1 < 0.14) {
				spawnItem(world, x, y, z, new ItemStack(ItemPlantFiber.block, 1));
			}
			if (rng1 < 0.02 && rand.nextDouble() < 0.51) {
				RNM = rand.nextDouble();
				if (RNM < 0.25) {	spawnItem(world, x, y, z, new ItemStack(Items.POISONOUS_POTATO, 1));} 
				else if (RNM< 0.39){spawnItem(world, x, y, z, new ItemStack(Items.CARROT, 			1));} 
				else if (RNM< 0.52){spawnItem(world, x, y, z, new ItemStack(Items.POTATO, 			1));} 
				else if (RNM< 0.77){spawnItem(world, x, y, z, new ItemStack(Items.WHEAT,			1));} 
				else if (RNM< 0.86){spawnItem(world, x, y, z, new ItemStack(Items.BEETROOT, 		1));} 
				else if (RNM< 0.93){spawnItem(world, x, y, z, new ItemStack(Blocks.BROWN_MUSHROOM,	1));} 
				else if (RNM< 0.98){spawnItem(world, x, y, z, new ItemStack(Blocks.RED_MUSHROOM,	1));} 
				else {				spawnItem(world, x, y, z, new ItemStack(Blocks.TALLGRASS, 		1, 1));}
			}
		} else if (block == Blocks.VINE && notShears) {
			if (rand.nextDouble() < 0.25) {
				spawnItem(world, x, y, z, new ItemStack(ItemCordageVine.block, 1));
			}
		} else if (block == Blocks.DIRT || block == Blocks.GRASS) {
			if (rand.nextDouble() < 0.035) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
				spawnItem(world, x, y, z, new ItemStack(ItemRock.block, 1));
			}
			if (rand.nextDouble() < 0.015) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
				spawnItem(world, x, y, z, new ItemStack(ItemRockFlat.block, 1));
			}
			if (rand.nextDouble() < 0.008) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
				spawnItem(world, x, y, z, new ItemStack(Items.FLINT, 1));
			}
		}
		}



		
		if (dim == CAVERN_DIM_ID && block == Blocks.MAGMA) {
			double chance = 0;
			chance = rand.nextDouble();
			if (((chance) < 0.2)) {
				world.setBlockState(pos, Blocks.LAVA.getDefaultState(), 3);
				if (((chance) < 0.1)) {
					Entity entityToSpawn = new EntityMagmaCube(world);

					entityToSpawn.setLocationAndAngles((x + 0.5), (y + 0.5), (z + 0.5), world.rand.nextFloat() * 360F, 0.0F);
					world.spawnEntity(entityToSpawn);
				}
			}
		}
	}
	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		BlockPos pos = event.getPos();
		executeProcedure(event.getWorld(), pos.getX(), pos.getY(), pos.getZ(), event.getPlayer(), event);
	}
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
