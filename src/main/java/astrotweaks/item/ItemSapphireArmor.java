package astrotweaks.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;


import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ItemSapphireArmor extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:sapphire_helmet")
	public static final Item helmet = null;
	@GameRegistry.ObjectHolder("astrotweaks:sapphire_chestplate")
	public static final Item body = null;
	@GameRegistry.ObjectHolder("astrotweaks:sapphire_leggings")
	public static final Item legs = null;
	@GameRegistry.ObjectHolder("astrotweaks:sapphire_boots")
	public static final Item boots = null;
	public ItemSapphireArmor(ElementsAstrotweaksMod instance) {
		super(instance, 18);
	}

	@Override
	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("SAPPHIRE_", "astrotweaks:sapphire_", 28, new int[]{3, 4, 5, 3}, 20,
				(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("item.armor.equip_diamond")), 0.5f);
		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.HEAD).setUnlocalizedName("sapphire_helmet").setRegistryName("sapphire_helmet"));
		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.CHEST).setUnlocalizedName("sapphire_body").setRegistryName("sapphire_chestplate"));
		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.LEGS).setUnlocalizedName("sapphire_legs").setRegistryName("sapphire_leggings"));
		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.FEET).setUnlocalizedName("sapphire_boots").setRegistryName("sapphire_boots"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("astrotweaks:sapphire_helmet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("astrotweaks:sapphire_body", "inventory"));
		ModelLoader.setCustomModelResourceLocation(legs, 0, new ModelResourceLocation("astrotweaks:sapphire_legs", "inventory"));
		ModelLoader.setCustomModelResourceLocation(boots, 0, new ModelResourceLocation("astrotweaks:sapphire_boots", "inventory"));
	}
}
