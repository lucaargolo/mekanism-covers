package dev.lucaargolo.mekanismcovers.mixin;

import com.google.common.collect.ImmutableList;
import dev.lucaargolo.mekanismcovers.CoverRenderType;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderType.class)
public class RenderTypeMixin {

    @Shadow @Final @Mutable
    private static ImmutableList<RenderType> CHUNK_BUFFER_LAYERS;

    @Inject(at = @At("TAIL"), method = "<clinit>")
    private static void onInit(CallbackInfo info) {
        ImmutableList.Builder<RenderType> builder = ImmutableList.builder();
        builder.addAll(CHUNK_BUFFER_LAYERS);
        builder.add(CoverRenderType.COVER);
        CHUNK_BUFFER_LAYERS = builder.build();
        int i = 0;
        for (var layer : CHUNK_BUFFER_LAYERS)
            ((RenderTypeAccessor) layer).setChunkLayerId(i++);
    }

}
