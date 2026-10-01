package dev.clientcapes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CapeConfig {
    public static final String NONE = "none";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("clientcapes.json");

    public static CapeConfig INSTANCE = new CapeConfig();

    public boolean enabled = true;
    public String selectedCape = NONE;

    public static void load() {
        try {
            if (Files.exists(PATH)) {
                CapeConfig loaded = GSON.fromJson(Files.readString(PATH), CapeConfig.class);
                if (loaded != null) INSTANCE = loaded;
            } else {
                INSTANCE.save();
            }
        } catch (Exception e) {
            System.err.println("[ClientCapes] Could not read config: " + e);
        }
    }

    public void save() {
        try {
            Files.writeString(PATH, GSON.toJson(this));
        } catch (IOException e) {
            System.err.println("[ClientCapes] Could not save config: " + e);
        }
    }
}
