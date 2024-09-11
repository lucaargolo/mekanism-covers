package dev.lucaargolo.mekanismcovers.mixin;

import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderType.class)
public interface RenderTypeAccessor {

    @Accessor(remap = false)
    void setChunkLayerId(int chunkLayerId);

}
