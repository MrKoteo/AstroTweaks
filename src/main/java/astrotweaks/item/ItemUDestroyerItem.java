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
import net.minecraft.util.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;

import net.minecraft.init.Blocks;
import net.minecraft.entity.Entity;

import java.util.Map;
import java.util.HashMap;


import astrotweaks.creativetab.TabAstroTweaks;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ItemUDestroyerItem extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:u_destroyer_item")
	public static final Item block = null;
	public ItemUDestroyerItem(ElementsAstrotweaksMod instance) { super(instance, 243); }
	@Override
	public void initElements() {
		elements.items.add(() -> new ItemCustom());
	}
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("astrotweaks:u_destroyer_item", "inventory"));
	}
	public static class ItemCustom extends Item {
		public ItemCustom() {
			setMaxDamage(1);
			//maxStackSize = 64;
			setUnlocalizedName("u_destroyer_item");
			setRegistryName("u_destroyer_item");
			setCreativeTab(TabAstroTweaks.tab);
		}

		@Override
		public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
            if (world.isRemote) {
                return EnumActionResult.PASS;
            }

            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);

            if (!player.capabilities.isCreativeMode) {
                ItemStack heldStack = player.getHeldItem(hand);
                heldStack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }
        @Override
        public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
            super.hitEntity(stack, target, attacker);

            if (!attacker.world.isRemote) {
                target.attackEntityFrom(DamageSource.GENERIC, 404.0f);
                if (!(attacker instanceof EntityPlayer && ((EntityPlayer) attacker).capabilities.isCreativeMode)) {
                    stack.shrink(1);
                }
            }
            return true;
        }
    }
}
