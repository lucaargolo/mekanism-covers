package dev.lucaargolo.mekanismcovers.compat;

import dev.lucaargolo.mekanismcovers.CoverRenderType;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;

public class CompatSodium {

    public static final TerrainRenderPass COVER_RENDER_PASS = new TerrainRenderPass(CoverRenderType.COVER, true, false);
    public static boolean IS_COVER_RENDER_PASS = false;

}
