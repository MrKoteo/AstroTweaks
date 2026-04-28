package astrotweaks.procedure;

import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ProcedureCoockedPufferfishEaten extends ElementsAstrotweaksMod.ModElement {
	public ProcedureCoockedPufferfishEaten(ElementsAstrotweaksMod instance) {
		super(instance, 344);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure CoockedPufferfishEaten!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		if (entity instanceof EntityLivingBase)
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, (int) 600, (int) 2, (false), (true)));
		if (entity instanceof EntityLivingBase)
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.POISON, (int) 700, (int) 4, (false), (true)));
		if (entity instanceof EntityLivingBase)
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.HUNGER, (int) 1200, (int) 2, (false), (true)));
	}
}
