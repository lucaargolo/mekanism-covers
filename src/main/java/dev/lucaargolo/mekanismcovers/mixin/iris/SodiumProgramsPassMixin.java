package dev.lucaargolo.mekanismcovers.mixin.iris;

import net.irisshaders.iris.pipeline.programs.SodiumPrograms;
import net.irisshaders.iris.shaderpack.loading.ProgramId;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(value = SodiumPrograms.Pass.class, remap = false)
public class SodiumProgramsPassMixin {

    @Invoker("<init>")
    public static SodiumPrograms.Pass invokeInit(String name, int id, ProgramId originalId) { throw new AssertionError(); }

    @Shadow @Final @Mutable
    private static SodiumPrograms.Pass[] $VALUES;

    static {
        ArrayList<SodiumPrograms.Pass> values =  new ArrayList<>(Arrays.asList($VALUES));
        SodiumPrograms.Pass last = values.getLast();

        // add new value
        SodiumPrograms.Pass newValue = invokeInit("COVER", last.ordinal() + 1, ProgramId.Water);
        values.add(newValue);

        $VALUES = values.toArray(new SodiumPrograms.Pass[0]);
    }

}
