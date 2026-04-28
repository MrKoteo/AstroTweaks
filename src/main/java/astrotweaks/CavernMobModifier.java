package astrotweaks.world;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.Entity;

import astrotweaks.AstrotweaksModVariables;

public class CavernMobModifier {
	public CavernMobModifier() {}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {

	    if (!event.getWorld().isRemote && event.getEntity() instanceof EntityLivingBase) {
	        EntityLivingBase entity = (EntityLivingBase) event.getEntity();
	        if (entity instanceof EntityPlayer || !(entity instanceof IMob)) return;
	        if (event.getWorld().provider.getDimension() != DepthsDim.DIMID) return;

			NBTTagCompound data = entity.getEntityData();
			if (data.getBoolean("Depths")) return;

	        IAttributeInstance AHealth = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
			IAttributeInstance ADamage = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			//IAttributeInstance ASpeed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			IAttributeInstance AArmor = entity.getEntityAttribute(SharedMonsterAttributes.ARMOR);

			double HPMult = 2.0D;

			if (AHealth != null) {
			    double oldBase = AHealth.getBaseValue();
			    double newBase = oldBase * HPMult;
			    AHealth.setBaseValue(newBase);

	            float newMax = (float) entity.getMaxHealth();
	            float current = entity.getHealth();

			    if (oldBase > 0.0D) {
			        float scaled = (float)(current * (newBase / oldBase));
			        entity.setHealth(Math.min(scaled, newMax));
			    } else {
			        entity.setHealth(newMax);
			    }
			}
			if (ADamage != null) {
			    ADamage.setBaseValue(ADamage.getBaseValue() * 2.0D);
			}
			if (AArmor != null) {
			    AArmor.setBaseValue(AArmor.getBaseValue() * 1.5D);
			}

			data.setBoolean("Depths", true);
		}
	}
}
