package astrotweaks.block.black_hole;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;



public class BlackHoleItemBlock extends ItemBlock {
    public BlackHoleItemBlock(Block block) {
        super(block);
        setMaxStackSize(1);
    }

    private static double lastTooltipMass = Double.NaN;
    private static String cachedMassLine;
    private static String cachedHorizonLine;

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        double mass = BlackHoleUtils.DEFAULT_MASS;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(BlackHoleTileEntity.TAG_MASS)) {
            mass = stack.getTagCompound().getDouble(BlackHoleTileEntity.TAG_MASS);
        }
        String line1, line2;
        if (Double.doubleToLongBits(mass) == Double.doubleToLongBits(lastTooltipMass) && cachedMassLine != null) {
            line1 = cachedMassLine;
            line2 = cachedHorizonLine;
        } else {
            line1 = TextFormatting.DARK_GRAY + "Mass: " + String.format("%.1f", mass);
            line2 = TextFormatting.GRAY + "Horizon: " + String.format("%.2f", BlackHoleUtils.getHorizonRadius(mass)) + " | Gravity: " + String.format("%.1f", BlackHoleUtils.getGravityRange(mass));
            lastTooltipMass = mass;
            cachedMassLine = line1;
            cachedHorizonLine = line2;
        }
        tooltip.add(line1);
        tooltip.add(line2);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(BlackHoleTileEntity.TAG_MASS)) {
            double m = stack.getTagCompound().getDouble(BlackHoleTileEntity.TAG_MASS);
            return super.getItemStackDisplayName(stack) + " [" + String.format("%.0f", m) + "]";
        }
        return super.getItemStackDisplayName(stack);
    }
}
