package astrotweaks.block.black_hole.client;

import astrotweaks.block.black_hole.BlackHoleTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

/**
 * Рендерит чёрные дыры на любой дистанции прорисовки, обходя отсечение
 * TileEntity по дистанции трансляции (64 блока) и frustum-culling.
 * isGlobalRenderer=true не спасает от отсечения по maxRenderDistance,
 * поэтому рендерим через RenderWorldLastEvent напрямую относительно камеры.
 */
@Mod.EventBusSubscriber(modid = "astrotweaks", value = Side.CLIENT)
public final class BlackHoleWorldRenderer {

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        World w = mc.world; if (w == null) return;
        java.util.Set<BlackHoleTileEntity> active = BlackHoleTileEntity.getActiveHoles();
        if (active.isEmpty()) return;
        Entity view = mc.getRenderViewEntity(); if (view == null) return;
        double px = view.lastTickPosX + (view.posX - view.lastTickPosX) * e.getPartialTicks();
        double py = view.lastTickPosY + (view.posY - view.lastTickPosY) * e.getPartialTicks();
        double pz = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * e.getPartialTicks();

        // Квадрат дистанции прогруженных чанков для этого игрока (renderDistance в чанках)
        int rd = mc.gameSettings.renderDistanceChunks;
        // +2 чанка запаса, чтобы не резать на границе прогрузки
        double loadedRange = (rd + 2) * 16.0;
        double loadedRangeSq = loadedRange * loadedRange;

        // GL-состояние захватываем один раз за кадр, а не на каждую BH:
        // внутри кадра оно не меняется между итерациями (ядро рендера
        // идемпотентно выставляет те же флаги), восстановление — один раз в finally.
        boolean fogWasEnabled = GL11.glIsEnabled(GL11.GL_FOG);
        boolean texWasEnabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        boolean cullWasEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        boolean lightWasEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean blendWasEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        int prevShade = GL11.glGetInteger(GL11.GL_SHADE_MODEL);

        try {
            // Прямая итерация CHM-множества: weakly-consistent, без CME и без копии.
            for (BlackHoleTileEntity bh : active) {
                if (bh.isInvalid() || bh.getWorld() != w) continue;
                BlockPos p = bh.getPos();
                // Stale-экземпляр после выгрузки чанка: позицию занял другой TE —
                // не рисуем призрака. Null (переходное состояние) — рисуем как раньше.
                TileEntity current = w.getTileEntity(p);
                if (current != null && current != bh) continue;
                // Только в прогруженных чанках для этого игрока
                if (!w.isBlockLoaded(p)) continue;
                // Доп. проверка по дистанции прогрузки — isBlockLoaded может держать чанк чуть дольше
                double dx = (p.getX() + 0.5) - px;
                double dy = (p.getY() + 0.5) - py;
                double dz = (p.getZ() + 0.5) - pz;
                double distSq = dx * dx + dy * dy + dz * dz;
                if (distSq > loadedRangeSq) continue;

                // Рендерим на любой дистанции прогрузки единым путём (без фантомного хенд-оффа 64)
                double x = p.getX() - px;
                double y = p.getY() - py;
                double z = p.getZ() - pz;
                BlackHoleTESR.renderStaticCore(bh, x, y, z, e.getPartialTicks());
            }
        } finally {
            GL11.glShadeModel(prevShade);

            if (fogWasEnabled) { GlStateManager.enableFog(); GL11.glEnable(GL11.GL_FOG); }
            else { GlStateManager.disableFog(); GL11.glDisable(GL11.GL_FOG); }

            if (lightWasEnabled) { GlStateManager.enableLighting(); GL11.glEnable(GL11.GL_LIGHTING); }
            else { GlStateManager.disableLighting(); GL11.glDisable(GL11.GL_LIGHTING); }

            if (cullWasEnabled) { GlStateManager.enableCull(); GL11.glEnable(GL11.GL_CULL_FACE); }
            else { GlStateManager.disableCull(); GL11.glDisable(GL11.GL_CULL_FACE); }

            if (blendWasEnabled) { GlStateManager.enableBlend(); GL11.glEnable(GL11.GL_BLEND); }
            else { GlStateManager.disableBlend(); GL11.glDisable(GL11.GL_BLEND); }

            if (texWasEnabled) { GlStateManager.enableTexture2D(); GL11.glEnable(GL11.GL_TEXTURE_2D); }
            else { GlStateManager.disableTexture2D(); GL11.glDisable(GL11.GL_TEXTURE_2D); }

            GlStateManager.color(1, 1, 1, 1);
        }
    }
}
