package astrotweaks.block.black_hole.client;

import astrotweaks.block.black_hole.BlackHoleUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;


/**
 * Клиентская половина вакуума чёрной дыры.
 *
 * <p>Задача — сделать расход кислорода видимым. Ванилла рисует полоску воздуха
 * только когда игрок реально в воде ({@code GuiIngameForge.renderAir}:
 * {@code isInsideOfMaterial(WATER)}), а {@code EntityLivingBase.onUpdate()} на
 * клиенте каждый тик делает {@code setAir(300)}, если игрок не в воде. Поэтому
 * одного серверного {@code setAir()} мало: нужно вернуть серверное значение
 * после тика и нарисовать пузырьки самому.
 *
 * <p>Порядок в тике (1.12.2): {@code onPreClientTick} (START) ->
 * {@code world.updateEntities()} (здесь ванила сбрасывает воздух в 300) ->
 * {@code onPostClientTick} (END) -> рендер. Поэтому значение воздуха, пришедшее
 * с сервера, запоминается в START и записывается обратно в END.
 *
 * <p>Зона вакуума на клиенте НЕ считается: масса дыры там может отставать, и
 * расчёт давал фантомные пузыри. Единственный источник истины — серверный
 * воздух: пока он меньше 300, сервер его сливает.
 */
@Mod.EventBusSubscriber(modid = "astrotweaks", value = Side.CLIENT)
public final class BlackHoleVacuumClientHandler {

    private static boolean inVacuum = false;
    /** Серверное значение воздуха, снятое до того, как ванила его сбросит. */
    private static int pendingAir = 300; // Полный запас воздуха (data-param AIR у Entity, как в ванилле).

    private BlackHoleVacuumClientHandler() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        if (player == null || mc.world == null) {
            inVacuum = false;
            return;
        }

        if (event.phase == TickEvent.Phase.START) {
            // Воздух приходит с сервера как data-param AIR; берём его ДО сброса в 300.
            pendingAir = player.getAir();
            // Вакуум определяем НЕ расчётом зоны на клиенте, а по факту: раз
            // сервер перестал держать 300 — значит он реально его сливает.
            // Клиентская масса дыры может отставать от серверной, из-за чего
            // расчётная зона была фантомной: пузыри есть, а воздух не тратится.
            inVacuum = pendingAir < 300 && !player.isInsideOfMaterial(Material.WATER);
            return;
        }

        // Пишем назад только когда сервер действительно сливает воздух. Иначе
        // затирали бы ванильный дренаж (в воде) намертво и полоску «замораживали».
        if (inVacuum) player.setAir(pendingAir);
    }

    @SubscribeEvent
    public static void onOverlayPost(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.AIR) return;
        if (!inVacuum) return;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        if (player == null) return;
        // В воде полоску рисует сама ванилла — дублировать нельзя.
        if (player.isInsideOfMaterial(Material.WATER)) return;

        int air = player.getAir();
        int full = MathHelper.ceil((air - 2) * 10.0D / 300);
        int popping = MathHelper.ceil(air * 10.0D / 300) - full;
        if (full <= 0 && popping <= 0) return;

        ScaledResolution sr = event.getResolution();
        int right = sr.getScaledWidth() / 2 + 91;
        int y = sr.getScaledHeight() - 39 - 10;

        mc.getTextureManager().bindTexture(Gui.ICONS);
        for (int i = 0; i < full + popping; ++i) {
            mc.ingameGUI.drawTexturedModalRect(right - i * 8 - 9, y, i < full ? 16 : 25, 18, 9, 9);
        }
    }
}
