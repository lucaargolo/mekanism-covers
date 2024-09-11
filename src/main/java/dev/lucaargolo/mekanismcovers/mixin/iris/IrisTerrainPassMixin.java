package dev.lucaargolo.mekanismcovers.mixin.iris;

import dev.lucaargolo.mekanismcovers.sodium.CustomTerrainRenderPasses;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.IrisTerrainPass;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(value = IrisTerrainPass.class, remap = false)
public class IrisTerrainPassMixin {

    @Invoker("<init>")
    public static IrisTerrainPass invokeInit(String name, int id, String shader) { throw new AssertionError(); }

    @Shadow @Final @Mutable
    private static IrisTerrainPass[] $VALUES;

    @Inject(at = @At("HEAD"), method = "toTerrainPass", cancellable = true)
    public void addCoverToTerrainPass(CallbackInfoReturnable<TerrainRenderPass> cir) {
        if(this.toString().equals("COVER")) {
            cir.setReturnValue(CustomTerrainRenderPasses.COVER);
        }
    }

    static {
        ArrayList<IrisTerrainPass> values =  new ArrayList<>(Arrays.asList($VALUES));
        IrisTerrainPass last = values.get(values.size() - 1);

        // add new value
        IrisTerrainPass newValue = invokeInit("COVER", last.ordinal() + 1, "gbuffers_water");
        values.add(newValue);

        $VALUES = values.toArray(new IrisTerrainPass[0]);
    }

}
