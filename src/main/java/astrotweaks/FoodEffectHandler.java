package astrotweaks.procedure;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.Random;


import astrotweaks.AstrotweaksModVariables;
import astrotweaks.ModVariables;


@Mod.EventBusSubscriber
public class FoodEffectHandler {
    // map  "item -> effect"
    private static Map<Item, PotionData[]> FOOD_EFFECTS = new HashMap<>();

    static {
        initEffects();
    }

	private static void initEffects() {
		if (!(AstrotweaksModVariables.Food_Negative_Effects)) { return; }
        // register
        // Format:  Name, Time in Ticks, Level
        addFoodEffects(Items.ROTTEN_FLESH,
            potion(MobEffects.WEAKNESS, 600, 0),
            potion(MobEffects.MINING_FATIGUE, 600, 0),
            potion(MobEffects.HUNGER, 1800, 1),
            potion(MobEffects.SLOWNESS, 600, 0),
            potion(MobEffects.NAUSEA, 200, 0),
            potion(MobEffects.POISON, 200, 0)
        );
        addFoodEffects(Items.POISONOUS_POTATO,
            potion(MobEffects.POISON, 200, 2),
            potion(MobEffects.WEAKNESS, 400, 0),
            potion(MobEffects.MINING_FATIGUE, 800, 0),
            potion(MobEffects.HUNGER, 900, 2),
            potion(MobEffects.SLOWNESS, 600, 0),
            potion(MobEffects.NAUSEA, 200, 1)
        );
        addFoodEffects(Items.SPIDER_EYE,
            potion(MobEffects.POISON, 300, 1),
            potion(MobEffects.WEAKNESS, 1200, 0),
            potion(MobEffects.MINING_FATIGUE, 400, 0),
            potion(MobEffects.HUNGER, 400, 2),
            potion(MobEffects.SLOWNESS, 600, 0),
            potion(MobEffects.NAUSEA, 100, 0)
        );
        // RAW meat
        if (ModVariables.Raw_Meat_Negative_Effects) {
        	
	        addFoodEffects(Items.PORKCHOP,
	            potion(MobEffects.WEAKNESS, 600, 0),
	            potion(MobEffects.MINING_FATIGUE, 600, 0),
	            potion(MobEffects.HUNGER, 1200, 2),
            	potion(MobEffects.POISON, 200, 0)
	        );
	        addFoodEffects(Items.BEEF,
	            potion(MobEffects.WEAKNESS, 400, 0),
	            potion(MobEffects.MINING_FATIGUE, 600, 0),
	            potion(MobEffects.HUNGER, 1200, 2),
            	potion(MobEffects.POISON, 200, 0)
	        );
	        addFoodEffects(Items.CHICKEN,
	            potion(MobEffects.WEAKNESS, 400, 0),
	            potion(MobEffects.MINING_FATIGUE, 600, 0),
	            potion(MobEffects.HUNGER, 1200, 1),
            	potion(MobEffects.POISON, 200, 0)
	        );
	        addFoodEffects(Items.RABBIT,
	            potion(MobEffects.WEAKNESS, 400, 0),
	            potion(MobEffects.MINING_FATIGUE, 600, 0),
	            potion(MobEffects.HUNGER, 1200, 2),
            	potion(MobEffects.POISON, 200, 0)
	        );
	        addFoodEffects(Items.MUTTON,
	            potion(MobEffects.WEAKNESS, 400, 0),
	            potion(MobEffects.MINING_FATIGUE, 600, 0),
	            potion(MobEffects.HUNGER, 1200, 1),
            	potion(MobEffects.POISON, 200, 0)
	        );
	        addFoodEffects(Items.FISH,
	            potion(MobEffects.WEAKNESS, 400, 0),
	            potion(MobEffects.MINING_FATIGUE, 600, 0),
	            potion(MobEffects.HUNGER, 1200, 2),
            	potion(MobEffects.POISON, 200, 0)
	        );

        }

        FOOD_EFFECTS = Collections.unmodifiableMap(new HashMap<>(FOOD_EFFECTS));
    }
    
    private static final PotionData potion(net.minecraft.potion.Potion potion, int duration, int amplifier) {
        return new PotionData(potion, duration, amplifier);
    }
	private static void addFoodEffects(Item item, PotionData... effects) {
	    FOOD_EFFECTS.put(item, effects);
	}

    @SubscribeEvent
    public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
    	if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
		PotionData[] effects = FOOD_EFFECTS.get(event.getItem().getItem());
    	if (effects == null) return;
    	EntityPlayer player = (EntityPlayer) event.getEntityLiving();

        for (PotionData data : effects) {
            player.addPotionEffect(new PotionEffect(data.potion, data.duration, data.amplifier));
        }
    }

    private static class PotionData {
        public final net.minecraft.potion.Potion potion;
        public final int duration;
        public final int amplifier;
        public PotionData(net.minecraft.potion.Potion potion, int duration, int amplifier) {
            this.potion = potion;
            this.duration = duration;
            this.amplifier = amplifier;
        }
    }
}