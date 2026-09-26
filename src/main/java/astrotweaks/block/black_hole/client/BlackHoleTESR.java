package astrotweaks.block.black_hole.client;

import astrotweaks.block.black_hole.BlackHoleTileEntity;
import astrotweaks.block.black_hole.BlackHoleUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;



public class BlackHoleTESR extends TileEntitySpecialRenderer<BlackHoleTileEntity> {

    private static BlackHoleShader shader;
    private static boolean shaderAttempted = false;

    private static BlackHoleShader getShader() {
        if (!shaderAttempted) {
            shaderAttempted = true;
            shader = BlackHoleShader.loadOrCreate();
        }
        return shader;
    }

    @Override
    public void render(BlackHoleTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        // TESR отключён — единый путь через RenderWorldLastEvent (иначе фантом на 64 блока: близко TESR, далеко WorldRenderer)
        // Оставлен no-op чтобы не дублировать и не пропадать при подходе. isGlobalRenderer всё ещё true на всякий.
    }

    /**
     * Статический рендер, используемый как из TESR, так и из RenderWorldLastEvent.
     * Позволяет обойти отсечение TileEntity по дистанции трансляции (64 блока)
     * и frustum-culling, рендеря на любой дистанции прорисовки.
     * Координаты x,y,z — уже относительно камеры (как в TESR).
     */
    public static void renderStatic(BlackHoleTileEntity te, double x, double y, double z, float partialTicks) {
        if (te == null || te.getWorld() == null) return;

        // Захватываем состояние до изменений, чтобы восстановить точно как было
        boolean fogWasEnabled = GL11.glIsEnabled(GL11.GL_FOG);
        boolean texWasEnabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        boolean cullWasEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        boolean lightWasEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean blendWasEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        int prevShade = GL11.glGetInteger(GL11.GL_SHADE_MODEL);

        try {
            renderStaticCore(te, x, y, z, partialTicks);
        } finally {
            // Восстанавливаем ровно то что было до рендера, синхронизируя GlStateManager + raw GL
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

    /**
     * Тело рендера без захвата/восстановления GL-состояния.
     * Вызывать только когда состояние уже захвачено снаружи (пакетный рендер
     * нескольких BH за кадр) либо из renderStatic. Оставляет конвейер в
     * детерминированном «рендер»-состоянии: те же флаги выставляются
     * безусловно при каждом вызове, поэтому повторные вызовы идемпотентны,
     * а восстановление выполняется один раз снаружи.
     */
    static void renderStaticCore(BlackHoleTileEntity te, double x, double y, double z, float partialTicks) {
        double mass = te.getMass();
        double horizon = BlackHoleUtils.getVisualHorizonRadius(mass);
        double gravRange = BlackHoleUtils.getGravityRange(mass);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);

        // Только необходимое: текстуры и бленд. Туман не трогаем (просьба убрать).
        GlStateManager.disableTexture2D();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_BLEND);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GlStateManager.disableLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.color(1, 1, 1, 1);

        BlackHoleShader sh = getShader();
        long worldTime = te.getWorld() != null ? te.getWorld().getTotalWorldTime() : 0;
        float time = (worldTime + partialTicks) * 0.05f;

        try {
            // --- Inner black horizon sphere (uMode=0 -> opaque black) ---
            if (sh != null) {
                sh.use();
                sh.setTime(time);
                sh.setHorizon((float) horizon);
                sh.setGravityRange((float) gravRange);
                sh.setMass((float) mass);
                sh.setMode(0.0f);
                BlackHoleRenderHelper.drawSphere(horizon, 0xFFFFFF, 1.0f, 32, 32);
            } else {
                BlackHoleRenderHelper.drawSphere(horizon, 0x000000, 1.0f, 32, 32);
            }

            double thickness = BlackHoleUtils.getHaloThickness(horizon);
            double halo1 = horizon + thickness;
            double halo2 = halo1 + thickness*0.8;
            double halo3 = halo2 + thickness*0.5;

            GlStateManager.depthMask(false);
            if (sh != null) {
                sh.setMode(1.0f); BlackHoleRenderHelper.drawSphere(halo1, 0xFFFFFF, 1.0f, 32, 32);
                sh.setMode(2.0f); BlackHoleRenderHelper.drawSphere(halo2, 0xFFFFFF, 1.0f, 32, 32);
                sh.setMode(3.0f); BlackHoleRenderHelper.drawSphere(halo3, 0xFFFFFF, 1.0f, 32, 32);
            } else {
                BlackHoleRenderHelper.drawSphere(halo1, 0x000000, 0.30f, 16, 16);
                BlackHoleRenderHelper.drawSphere(halo2, 0x000000, 0.16f, 16, 16);
                BlackHoleRenderHelper.drawSphere(halo3, 0x000000, 0.02f, 16, 16);
            }
        } finally {
            if (sh != null) BlackHoleShader.stop();
            // Core оставляет детерминированное «рендер»-состояние; полное
            // восстановление делает внешний код (renderStatic либо bulk-цикл).
            GlStateManager.depthMask(true);
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.popMatrix();
        }
    }

    /**
     * Совместимость: старый вызов из BlackHoleWorldRenderer (renderAt).
     * Делегирует в renderStatic.
     */
    public static void renderAt(BlackHoleTileEntity te, double x, double y, double z, float partialTicks) {
        // x,y,z здесь уже camera-relative (pos - cam), TESR ожидает такой же формат,
        // но внутри делает +0.5. WorldRenderer передает p+0.5 - cam, поэтому вычитаем 0.5
        // чтобы избежать двойного сдвига. Однако TESR.renderStatic делает +0.5 сам,
        // а WorldRenderer ранее передавал p+0.5-cam. Чтобы сохранить совместимость,
        // считаем что сюда приходит уже x = p+0.5 - cam, тогда внутри renderStatic
        // не должен добавляться ещё один 0.5. Проще: вызвать renderStatic с x-0.5.
        renderStatic(te, x - 0.5, y - 0.5, z - 0.5, partialTicks);
    }

    @Override
    public boolean isGlobalRenderer(BlackHoleTileEntity te) {
        return true; // allow rendering outside chunk frustum culling via bounding box
    }
}
