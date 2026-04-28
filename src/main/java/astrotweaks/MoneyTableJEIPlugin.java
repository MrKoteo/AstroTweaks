package astrotweaks.jei;

import mezz.jei.api.*;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.Loader;
import astrotweaks.block.BlockMoneyTable;
import astrotweaks.gui.GuiMTGUI;
import astrotweaks.item.*;

import javax.annotation.Nullable;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.Minecraft;

public class MoneyTableJEIPlugin {
    public static void init() {
        // empty - ok
    }

    @JEIPlugin
    public static class Plugin implements IModPlugin {
        @Override
        public void registerCategories(IRecipeCategoryRegistration registry) {
            IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
            registry.addRecipeCategories(new Category(guiHelper));
        }

        @Override
        public void register(IModRegistry registry) {
            registry.addAdvancedGuiHandlers(new GuiHandler[] {new GuiHandler()});
            registry.addRecipes(getAllRecipes(), Category.UID);
            registry.addRecipeClickArea(GuiMTGUI.GuiWindow.class, 76, 10, 24, 16, Category.UID);
            registry.addRecipeCatalyst(new ItemStack(BlockMoneyTable.block), Category.UID);
        }
    }

    public static class Category implements IRecipeCategory<RecipeWrapper> {
        public static final String UID = "astrotweaks.money_table";
        private final IDrawable background;

        public Category(IGuiHelper guiHelper) {
		    final ResourceLocation texture = new ResourceLocation("astrotweaks", "textures/mtjei.png");
		    final int originalWidth = 178;
		    final int originalHeight = 90;
		    final float scale = 1.0f;  // scale multiplier
		
		    this.background = new IDrawable() {
		        @Override
		        public int getWidth() {
		            return (int) (originalWidth * scale);
		        }
		
		        @Override
		        public int getHeight() {
		            return (int) (originalHeight * scale);
		        }
		
		        @Override
		        public void draw(Minecraft minecraft, int xOffset, int yOffset) {
					GlStateManager.disableBlend();
		        	
		            GlStateManager.pushMatrix();
		            GlStateManager.translate(xOffset, yOffset, 0);
		            GlStateManager.scale(scale, scale, 1.0f);
		            
		            minecraft.getTextureManager().bindTexture(texture);
		
		            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		            buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		            buffer.pos(0, originalHeight, 0).tex(0, 1).endVertex();
		            buffer.pos(originalWidth, originalHeight, 0).tex(1, 1).endVertex();
		            buffer.pos(originalWidth, 0, 0).tex(1, 0).endVertex();
		            buffer.pos(0, 0, 0).tex(0, 0).endVertex();
		            Tessellator.getInstance().draw();
		
		            GlStateManager.popMatrix();
		            GlStateManager.enableBlend();
		        }
		    };
		}

        @Override
        public String getUid() {
            return UID;
        }

        @Override
        public String getTitle() {
            return I18n.translateToLocal("JEI.container.money_table");
        }

        @Override
        public String getModName() {
            return "AstroTweaks";
        }

        @Override
        public IDrawable getBackground() {
            return background;
        }

        @Override
        public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapper recipeWrapper, IIngredients ingredients) {
            IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

            guiItemStacks.init(0, true, 53, 9);		// in +
            guiItemStacks.init(1, false, 105, 9);	// out +
            guiItemStacks.init(2, true, 53, 36);	// in -
            guiItemStacks.init(3, false, 105, 36);	// out -
            guiItemStacks.init(4, true, 20, 61);	// gavel

			// dynamic 'set' for (recipeWrapper.isIncrease)
            List<ItemStack> inputs = recipeWrapper.inputs;
        	List<ItemStack> outputs = recipeWrapper.outputs;

        	if (recipeWrapper.isIncrease > 0) {
	            guiItemStacks.set(0, inputs.get(0));  // in (10x low)
	            guiItemStacks.set(1, outputs.get(0)); // out (1x high)
	            guiItemStacks.set(4, inputs.get(1));  // tool (damaged)
	            // hide 2 & 3
	            guiItemStacks.set(2, ItemStack.EMPTY);
	            guiItemStacks.set(3, ItemStack.EMPTY);
	        } else {
	            guiItemStacks.set(2, inputs.get(0));  // in (1x high)
	            guiItemStacks.set(3, outputs.get(0)); // out (10x low)
	            guiItemStacks.set(4, inputs.get(1));  // tool
	            // hide 0 & 1
	            guiItemStacks.set(0, ItemStack.EMPTY);
	            guiItemStacks.set(1, ItemStack.EMPTY);
	        }

            guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
                if (slotIndex == 4) {
                    tooltip.add(I18n.translateToLocal("JEI.tooltip.consumes_durability"));
                }
            });
        }
    }

    public static class RecipeWrapper implements IRecipeWrapper {
        private final List<ItemStack> inputs = new ArrayList<>();
        private final List<ItemStack> outputs = new ArrayList<>();
        public final int isIncrease; // recipe type

        public RecipeWrapper(ItemStack input, ItemStack output, ItemStack tool, int isIncrease) {
	        this.isIncrease = isIncrease;  // init
	
	        if (isIncrease == 1) {
	            inputs.add(new ItemStack(input.getItem(), 10));  // 10x in
	            outputs.add(new ItemStack(output.getItem(), 1)); // 1x out
	        } else if (isIncrease == 0) {
	            inputs.add(new ItemStack(input.getItem(), 1));   // 1x in
	            outputs.add(new ItemStack(output.getItem(), 10)); // 10x out
	        } else {
	            inputs.add(new ItemStack(input.getItem(), 1));   // 1x in
	            outputs.add(new ItemStack(output.getItem(), 1)); // 10x out
	        }
	        
	        ItemStack damagedTool = tool.copy();
	        damagedTool.setItemDamage(tool.getItemDamage() + 1);
	        inputs.add(damagedTool);
	    }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInputLists(ItemStack.class, Collections.singletonList(inputs));
            ingredients.setOutputLists(ItemStack.class, Collections.singletonList(outputs));
        }
    }

    public static class GuiHandler implements IAdvancedGuiHandler<GuiMTGUI.GuiWindow> {
        @Override
        public Class<GuiMTGUI.GuiWindow> getGuiContainerClass() {
            return GuiMTGUI.GuiWindow.class;
        }

        @Nullable  // + @Nullable for FIX "type mismatch"
        @Override
        public List<Rectangle> getGuiExtraAreas(GuiMTGUI.GuiWindow guiContainer) {
            return Collections.emptyList();
        }

        @Nullable  // + @Nullable
        @Override
        public Object getIngredientUnderMouse(GuiMTGUI.GuiWindow guiContainer, int mouseX, int mouseY) {
            return null;
        }
    }

    private static List<RecipeWrapper> getAllRecipes() {
        List<RecipeWrapper> recipes = new ArrayList<>();
        ItemStack hammer = new ItemStack(ItemGavel.block);

        ItemStack[][] coinPairs = {
                {new ItemStack(ItemWoodCoin.block), new ItemStack(ItemStoneCoin.block)},
                {new ItemStack(ItemStoneCoin.block), new ItemStack(ItemCopperCoin.block)},
                {new ItemStack(ItemCopperCoin.block), new ItemStack(ItemSilverCoin.block)},
                {new ItemStack(ItemSilverCoin.block), new ItemStack(ItemGoldCoin.block)},
                {new ItemStack(ItemGoldCoin.block), new ItemStack(ItemPlatinumCoin.block)},
                {new ItemStack(ItemPlatinumCoin.block), new ItemStack(ItemDiamantCoin.block)},
                {new ItemStack(ItemDiamantCoin.block), new ItemStack(ItemPalladiumCoin.block)},
                {new ItemStack(ItemPalladiumCoin.block), new ItemStack(ItemEluniteCoin.block)},
                {new ItemStack(ItemEluniteCoin.block), new ItemStack(ItemMythrilCoin.block)},
                {new ItemStack(ItemMythrilCoin.block), new ItemStack(ItemAdamantiumCoin.block)},
                {new ItemStack(ItemAdamantiumCoin.block), new ItemStack(ItemUniCoin.block)}
        };

		//recipes.add(new RecipeWrapper(new ItemStack(ItemCopperPlate.block), new ItemStack(ItemCopperCoin.block), hammer, false));
		
        for (ItemStack[] pair : coinPairs) {
            ItemStack low = pair[0];
            ItemStack high = pair[1];

            recipes.add(new RecipeWrapper(low, high, hammer, 1));
            recipes.add(new RecipeWrapper(high, low, hammer, 0));
        }

        recipes.add(new RecipeWrapper(new ItemStack(ItemCopperPlate.block), new ItemStack(ItemCopperCoin.block), hammer, 2));
        
        return recipes;
    }
}