package astrotweaks.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import astrotweaks.creativetab.TabAstroTweaks;
import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ItemVoidAntimatter extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:void_antimatter")
	public static final Item block = null;
	public ItemVoidAntimatter(ElementsAstrotweaksMod instance) { super(instance, 74); }

	// Timer for limit max execs per sec (thread-safe)
	private static final Map<Integer, Long> lastUseMs = new ConcurrentHashMap<>();
	@Override public void initElements() { elements.items.add(() -> new ItemCustom()); }

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("astrotweaks:void_antimatter", "inventory"));
	}

	public static class ItemCustom extends Item {
		public ItemCustom() {
			setUnlocalizedName("void_antimatter");
			setRegistryName("void_antimatter");
			setCreativeTab(TabAstroTweaks.tab);
		}
		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> tooltip, ITooltipFlag flag) {
			super.addInformation(itemstack, world, tooltip, flag);
			tooltip.add(TextFormatting.RED + new TextComponentTranslation("item.void_antimatter.tooltip").getFormattedText());
		}
		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int slot, boolean par5) {
			super.onUpdate(itemstack, world, entity, slot, par5);

			if (!(entity instanceof EntityLivingBase)) return;
			// Only react for the item instance held in main hand
			EntityLivingBase living = (EntityLivingBase) entity;
			ItemStack main = living.getHeldItemMainhand();
			if (main == null || main == ItemStack.EMPTY) return;
			// Compare item types (not NBT/count)
			if (main.getItem() != ItemVoidAntimatter.block) return;

			if (world.isRemote) return;

			int id = entity.getEntityId();
			long now = System.currentTimeMillis();
			Long last = lastUseMs.get(id);
			if (last != null && now - last < 500) return; // rate limit: 500 ms
			lastUseMs.put(id, now);

			// Execute explosion / item removal logic
			AMExplode(living, world);
		}

		// Remove held antimatter items and create explosion at entity position (server-side)
		private static void AMExplode(EntityLivingBase entity, World world) {
			boolean mainIsVoid = entity.getHeldItemMainhand() != null && entity.getHeldItemMainhand().getItem() == ItemVoidAntimatter.block;
			boolean offIsVoid = entity.getHeldItemOffhand() != null && entity.getHeldItemOffhand().getItem() == ItemVoidAntimatter.block;

			// If both hands hold the item -> clear both; else clear main only
			if (offIsVoid && mainIsVoid) {
				// Clear offhand
				entity.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Blocks.AIR));
				// Clear mainhand
				entity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Blocks.AIR));
			} else if (mainIsVoid) {
				entity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Blocks.AIR));
			} else {
				return;
			}
			// If player, mark inventory dirty so change syncs
			if (entity instanceof EntityPlayerMP) {
				((EntityPlayerMP) entity).inventory.markDirty();
			}
			// Explosion at entity integer coordinates, power 13, causes block damage
			int x = (int) Math.floor(entity.posX);
			int y = (int) Math.floor(entity.posY);
			int z = (int) Math.floor(entity.posZ);
			if (!world.isRemote) {
				world.createExplosion(null, x, y, z, 13.0f, true);
			}
		}
	}
}
