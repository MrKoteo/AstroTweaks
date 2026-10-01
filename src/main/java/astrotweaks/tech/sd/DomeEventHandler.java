package astrotweaks.tech.sd;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;



/** Чистит World-ключи DomeManager при выгрузке мира (иначе утечка). */
@Mod.EventBusSubscriber(modid = "astrotweaks")
public class DomeEventHandler {
    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld() != null) DomeManager.removeWorld(event.getWorld());
    }
}
