package dev.lucaargolo.mekanismcovers.compat;

import dev.lucaargolo.mekanismcovers.ModConfig;
import net.neoforged.fml.loading.FMLLoader;
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
        if(!ModConfig.getInstance().isDisableAdvancedLayer()) {
            boolean nvidiumEnabled = FMLLoader.getLoadingModList().getModFileById("nvidium") != null;
            nvidiumEnabled |= FMLLoader.getLoadingModList().getModFileById("acedium") != null;
            nvidiumEnabled |= FMLLoader.getLoadingModList().getModFileById("revidium") != null;
            try {
                Thread.currentThread().getContextClassLoader().loadClass("me.cortex.nvidium.sodiumCompat.SodiumResultCompatibility");
                nvidiumEnabled = true;
            } catch (ClassNotFoundException ignored) { }
            if(nvidiumEnabled) {
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
        }

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
        }else if(mixinClass.equals("dev.lucaargolo.mekanismcovers.mixin.LevelRendererMixin")) {
            boolean sodiumEnabled = FMLLoader.getLoadingModList().getModFileById("sodium") != null;
            sodiumEnabled |= FMLLoader.getLoadingModList().getModFileById("embeddium") != null;
            sodiumEnabled |= FMLLoader.getLoadingModList().getModFileById("xenon") != null;
            try {
                Thread.currentThread().getContextClassLoader().loadClass("net.caffeinemc.mods.sodium.mixin.core.render.world.LevelRendererMixin");
                sodiumEnabled = true;
            } catch (ClassNotFoundException | NoClassDefFoundError ignored) { }
            if(sodiumEnabled) {
                System.out.println("[Mekanism Covers] Sodium is present. Disabling "+mixinClass);
                return false;
            }
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
