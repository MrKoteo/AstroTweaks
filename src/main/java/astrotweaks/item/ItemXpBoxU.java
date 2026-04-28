package astrotweaks.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;

import java.util.Map;
import java.util.HashMap;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ItemXpBoxU extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:xp_box")
	public static final Item block = null;
	public ItemXpBoxU(ElementsAstrotweaksMod instance) {
		super(instance, 631);
	}
	@Override
	public void initElements() {
		elements.items.add(() -> new ItemCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("astrotweaks:xp_box", "inventory"));
	}
	public static class ItemCustom extends Item {
		public ItemCustom() {
			setUnlocalizedName("xp_box");
			setRegistryName("xp_box");
		}

		@Override
		@SideOnly(Side.CLIENT)
		public boolean hasEffect(ItemStack itemstack) {
			return true;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			ItemStack itemstack = entity.getHeldItem(hand);
			if (!world.isRemote) {
				OpenBox(entity, itemstack);
				itemstack.shrink(1);
			}
			return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
		}
		@Override
		public EnumActionResult onItemUseFirst(EntityPlayer entity, World world, BlockPos pos, EnumFacing direction, float hitX, float hitY,
				float hitZ, EnumHand hand) {
			ItemStack itemstack = entity.getHeldItem(hand);
			if (!world.isRemote) {
				OpenBox(entity, itemstack);
				itemstack.shrink(1);
			}
			return EnumActionResult.SUCCESS;
		}

		private static void OpenBox(Entity entity, ItemStack itemstack) {
			double XpAmount = 0;
			boolean Type = false;
			XpAmount = itemstack.hasTagCompound() ? itemstack.getTagCompound().getDouble("xp") : -1;
			Type = (itemstack.hasTagCompound() && itemstack.getTagCompound().getBoolean("levels"));

			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (Type) {
					player.addExperienceLevel((int) XpAmount);
				} else {
					player.addExperience((int) XpAmount);
				}
			}
		}
	}
}
