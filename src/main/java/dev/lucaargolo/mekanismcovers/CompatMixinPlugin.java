package dev.lucaargolo.mekanismcovers;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class CompatMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String s) {
        ModConfig.load();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targeClass, String mixinClass) {
        try {
            Thread.currentThread().getContextClassLoader().loadClass("me.cortex.nvidium.sodiumCompat.SodiumResultCompatibility");
            if(!ModConfig.getInstance().isDisableAdvancedLayer()) {
                System.out.println("==================================================================");
                System.out.println("                             WARNING!                             ");
                System.out.println("                         Mekanism  Covers                         ");
                System.out.println("==================================================================");
                System.out.println("Nvidium has been found in your Minecraft installation. Advanced   ");
                System.out.println("cover rendering will be disabled.                                 ");
                System.out.println("                                                                  ");
                System.out.println("Holding a wrench/cable in your hand will no longer make covers get");
                System.out.println("translucent. They will instead show an alternative model.         ");
                System.out.println("==================================================================");
                ModConfig.getInstance().setDisableAdvancedLayer(true);
            }
        } catch (ClassNotFoundException ignored) { }
        try {
            Thread.currentThread().getContextClassLoader().loadClass("me.jellysquid.mods.sodium.mixin.core.render.world.WorldRendererMixin");
            if(mixinClass.equals("dev.lucaargolo.mekanismcovers.mixin.LevelRendererMixin")) {
                System.out.println("[Mekanism Covers] Sodium is present. Disabling "+mixinClass);
                return false;
            }
        } catch (ClassNotFoundException ignored) { }
        if(ModConfig.getInstance().isDisableAdvancedLayer()) {
            if(mixinClass.equals("dev.lucaargolo.mekanismcovers.mixin.LevelRendererMixin")) {
                System.out.println("[Mekanism Covers] Advanced Layer is disabled. Disabling "+mixinClass);
                return false;
            }else if(mixinClass.equals("dev.lucaargolo.mekanismcovers.mixin.RenderTypeAccessor")) {
                System.out.println("[Mekanism Covers] Advanced Layer is disabled. Disabling "+mixinClass);
                return false;
            }else if(mixinClass.equals("dev.lucaargolo.mekanismcovers.mixin.RenderTypeMixin")) {
                System.out.println("[Mekanism Covers] Advanced Layer is disabled. Disabling "+mixinClass);
                return false;
            }else if(mixinClass.startsWith("dev.lucaargolo.mekanismcovers.mixin.iris")) {
                System.out.println("[Mekanism Covers] Advanced Layer is disabled. Disabling "+mixinClass);
                return false;
            }else if (mixinClass.startsWith("dev.lucaargolo.mekanismcovers.mixin.sodium")) {
                System.out.println("[Mekanism Covers] Advanced Layer is disabled. Disabling "+mixinClass);
                return false;
            };
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

}
