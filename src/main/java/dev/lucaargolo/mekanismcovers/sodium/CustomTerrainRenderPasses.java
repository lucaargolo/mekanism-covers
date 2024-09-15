package dev.lucaargolo.mekanismcovers.sodium;

import dev.lucaargolo.mekanismcovers.CoverRenderType;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;

public class CustomTerrainRenderPasses {

    public static final TerrainRenderPass COVER = new TerrainRenderPass(CoverRenderType.COVER, true, false);


}
