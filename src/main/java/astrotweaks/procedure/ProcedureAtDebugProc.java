/*
package astrotweaks.procedure;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;

import astrotweaks.ElementsAstrotweaksMod;
import astrotweaks.AstrotweaksModVariables;
import astrotweaks.ModVariables;

@ElementsAstrotweaksMod.ModElement.Tag
public class ProcedureAtDebugProc extends ElementsAstrotweaksMod.ModElement {
    public ProcedureAtDebugProc(ElementsAstrotweaksMod instance) {
        super(instance, 499);
    }

    public static void executeProcedure(Map<String, Object> dependencies) {
        if (dependencies.get("entity") == null) {
            System.err.println("Failed to exec AtDebugProc!");
            return;
        }

        Entity entity = (Entity) dependencies.get("entity");

        String message = "\n" +
        	"AstroTech_Environment: " + AstrotweaksModVariables.AstroTech_Environment + "\n" +
            "EnableProgressionSystem: " + AstrotweaksModVariables.EnableProgressionSystem + "\n" +

            "Overworld_Quartz_Generation: " + AstrotweaksModVariables.Overworld_Quartz_Generation + "\n" +
            "Ruby_Generation: " + AstrotweaksModVariables.Ruby_Generation + "\n" +
            "Enable_SnowVillages: " + ModVariables.Enable_SnowVillages + "\n" +
            "Enable_ForestVillages: " + ModVariables.Enable_ForestVillages + "\n" +
            "Enable_Ground_Elements: " + ModVariables.Enable_Ground_Elements + "\n" +
            "Stick_Gen_Attempts: " + ModVariables.Stick_Gen_Attempts + "\n" + 
            "Stick_Gen_Heights: Min =" + ModVariables.Stick_Gen_Min_Y + ", Max =" + ModVariables.Stick_Gen_Max_Y + "\n" +
            "Stick_Gen_Biomes: " + ModVariables.Stick_Gen_Biomes + "\n\n" +


            
            "Rock_Gen_Attempts: " + ModVariables.Rock_Gen_Attempts + "\n" +
            "Rock_Gen_Heights: Min =" + ModVariables.Rock_Gen_Min_Y + ", Max =" + ModVariables.Rock_Gen_Max_Y + "\n" +
            "Rock_Gen_Biomes: " + ModVariables.Rock_Gen_Biomes + "\n\n" +



            "Money_Can_Smelt: " + AstrotweaksModVariables.Money_Can_Smelt + "\n" +
            "Money_Can_Craft: " + AstrotweaksModVariables.Money_Can_Craft + "\n" +
            "Food_Negative_Effects: " + AstrotweaksModVariables.Food_Negative_Effects + "\n";

        if (entity instanceof EntityPlayer && !entity.world.isRemote) {
            ((EntityPlayer) entity).sendStatusMessage(new TextComponentString(message), false);
        }

        System.out.println("Debug Output:\n\n\n" + message + "\n\n");
    }
}*/