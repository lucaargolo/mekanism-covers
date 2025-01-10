
package dev.lucaargolo.mekanismcovers;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.neoforged.fml.loading.FMLPaths;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;

@SuppressWarnings({"CallToPrintStackTrace", "FieldCanBeLocal", "FieldMayBeFinal", "SameParameterValue"})
public class ModConfig {

    private static final String CONFIG_PATH = FMLPaths.CONFIGDIR.get().toString() + "/" + "mekanismcovers.json";
    private static ModConfig instance;

    public static ModConfig getInstance() {
        return instance;
    }

    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();

    private boolean disableAdvancedCoverRendering = false;
    private boolean enableShaderCompatibleRendering = false;

    public boolean isDisableAdvancedCoverRendering() {
        return disableAdvancedCoverRendering;
    }

    public boolean isEnableShaderCompatibleRendering() {
        return enableShaderCompatibleRendering;
    }

    public void setDisableAdvancedCoverRendering(boolean disableAdvancedLayer) {
        this.disableAdvancedCoverRendering = disableAdvancedLayer;
    }

    public void setShaderCompatibleRendering(boolean enable) {
        this.enableShaderCompatibleRendering = enable;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            GSON.toJson(this, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        try (FileReader reader = new FileReader(CONFIG_PATH)) {
            Type mapType = new TypeToken<ModConfig>(){}.getType();
            instance = GSON.fromJson(reader, mapType);
        } catch (FileNotFoundException e) {
            if (tryCreateFile(CONFIG_PATH)) {
                instance = new ModConfig();
                instance.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static boolean tryCreateFile(String p) {
        Path filePath = Path.of(p);
        String[] ps = p.split("/");
        StringBuilder fs = new StringBuilder();
        for(int i = 0; i < ps.length-1; i++) {
            fs.append(ps[i]).append("/");
        }
        Path folderPath = Path.of(fs.toString());
        try {
            folderPath.toFile().mkdirs();
            filePath.toFile().createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}