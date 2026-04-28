/*
package astrotweaks;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

import javax.annotation.Nullable;
import java.util.Map;

@Name("AstroTweaks CoreMod")
@TransformerExclusions({"astrotweaks"})
@MCVersion("1.12.2")
@SortingIndex(1001) // high priority
public class CoreModLoader implements IFMLLoadingPlugin {
    // debug
    public CoreModLoader() {
        System.out.println("=== CoreModLoader constructor called ===");
    }
    
    @Override
    public String[] getASMTransformerClass() {
        //System.out.println("=== [AstroTweaks] getASMTransformerClass called ===");
        return new String[]{"astrotweaks.tweaks.PathNodeTransformer"};
    }
    
    @Override
    public String getModContainerClass() {
        //System.out.println("=== [AstroTweaks] getModContainerClass called ===");
        // return "astrotweaks.AstroTweaksCoreContainer";
        return null;
    }
    
    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }
    
    @Override
    public void injectData(Map<String, Object> data) {
        //System.out.println("=== [AstroTweaks] injectData called ===");
        // debug: print all data
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            System.out.println("Data: " + entry.getKey() + " = " + entry.getValue());
        }
    }
    
    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
*/